import os
import uuid
from flask import Flask, request, jsonify, abort
import requests
from datetime import datetime
from pymongo import MongoClient

app = Flask(__name__)

# Get the port from the environment variable or default to 5000
port = os.getenv('FLASK_PORT', 5000)

# Get the collection name from the environment variable
collection_name = os.getenv('COLLECTION_NAME', 'default_collection')  # Default to 'default_collection' if not set

# Initialize MongoDB client
MONGO_URI = os.getenv('MONGO_URI', 'mongodb://mongo:27017/')
client = MongoClient(MONGO_URI)
db = client["stock_service"]

# Access the appropriate collection
collection = db[collection_name]

# Load API-Ninjas API Key from environment variable
API_KEY = os.getenv('API_KEY')
if not API_KEY:
    raise Exception("API_KEY not set in environment variables.")

API_NINJAS_URL = "https://api.api-ninjas.com/v1/stockprice"

def format_float(value):
    """Format float to two decimal places."""
    return round(float(value), 2)

def validate_date(date_text):
    """Validate date format DD-MM-YYYY."""
    try:
        datetime.strptime(date_text, '%d-%m-%Y')
        return True
    except ValueError:
        return False

@app.route('/stocks/clear', methods=['POST'])
def clear_stocks():
    collection.delete_many({})
    return jsonify({"message": "All stocks cleared"}), 200

@app.route('/stocks', methods=['POST'])
def add_stock():
    if not request.is_json:
        return jsonify({"error": "Expected application/json media type"}), 415

    data = request.get_json()

    # Required fields
    required_fields = ['symbol', 'purchase price', 'shares']
    for field in required_fields:
        if field not in data:
            return jsonify({"error": "Malformed data"}), 400

    symbol = data['symbol'].upper()
    purchase_price = data['purchase price']
    shares = data['shares']

    # Optional fields
    name = data.get('name', 'NA')
    purchase_date = data.get('purchase date', 'NA')

    # Validate data types
    if not isinstance(symbol, str) or not isinstance(name, str):
        return jsonify({"error": "Malformed data"}), 400
    if not isinstance(purchase_price, (int, float)) or not isinstance(shares, int):
        return jsonify({"error": "Malformed data"}), 400
    if purchase_date != 'NA' and not validate_date(purchase_date):
        return jsonify({"error": "Malformed data"}), 400

    # Generate unique ID
    stock_id = str(uuid.uuid4())

    # Create stock object
    stock = {
        "_id": stock_id,  # MongoDB's primary key
        "id": stock_id,   # Explicitly set custom ID field
        "name": name,
        "symbol": symbol,
        "purchase price": format_float(purchase_price),
        "purchase date": purchase_date,
        "shares": shares
    }

    # Add to storage
    collection.insert_one(stock)

    return jsonify({"id": stock_id}), 201

@app.route('/stocks', methods=['GET'])
def get_stocks():
    query_params = request.args.to_dict()

    # Build MongoDB query
    mongo_query = {}
    for key, value in query_params.items():
        if key in ['id', 'name', 'symbol', 'purchase price', 'purchase date', 'shares']:
            if key in ['purchase price', 'shares']:
                mongo_query[key] = float(value) if '.' in value else int(value)
            else:
                mongo_query[key] = value

    # Fetch from MongoDB and exclude `_id` field
    stocks = list(collection.find(mongo_query, {'_id': 0}))
    return jsonify(stocks), 200

@app.route('/stocks/<stock_id>', methods=['GET'])
def get_stock(stock_id):
    stock = collection.find_one({"_id": stock_id}, {'_id': 0})
    if not stock:
        return jsonify({"error": "Not found"}), 404
    return jsonify(stock), 200

@app.route('/stocks/<stock_id>', methods=['PUT'])
def update_stock(stock_id):
    if not request.is_json:
        return jsonify({"error": "Expected application/json media type"}), 415

    data = request.get_json()

    # Validate the `id` field in the request body
    if data.get('id') != stock_id:
        return jsonify({"error": "Malformed data"}), 400

    # Required fields
    required_fields = ['id', 'symbol', 'purchase price', 'shares', 'name', 'purchase date']
    for field in required_fields:
        if field not in data:
            return jsonify({"error": f"Field '{field}' is missing"}), 400

    # Validate data types
    symbol = data['symbol'].upper()
    purchase_price = data['purchase price']
    shares = data['shares']
    name = data['name']
    purchase_date = data['purchase date']

    if not isinstance(symbol, str) or not isinstance(name, str):
        return jsonify({"error": "Malformed data"}), 400
    if not isinstance(purchase_price, (int, float)) or not isinstance(shares, int):
        return jsonify({"error": "Malformed data"}), 400
    if purchase_date != 'NA' and not validate_date(purchase_date):
        return jsonify({"error": "Malformed data"}), 400

    # Update the stock in MongoDB
    result = collection.update_one(
        {"_id": stock_id},
        {"$set": {
            "name": name,
            "symbol": symbol,
            "purchase price": format_float(purchase_price),
            "purchase date": purchase_date,
            "shares": shares
        }}
    )

    if result.matched_count == 0:
        return jsonify({"error": "Not found"}), 404

    return jsonify({"id": stock_id}), 200

@app.route('/stocks/<stock_id>', methods=['DELETE'])
def delete_stock(stock_id):
    result = collection.delete_one({"_id": stock_id})
    if result.deleted_count == 0:
        return jsonify({"error": "Not found"}), 404
    return '', 204

@app.route('/stock-value/<stock_id>', methods=['GET'])
def get_stock_value(stock_id):
    # Retrieve the stock object from the database
    stock = collection.find_one({"_id": stock_id})
    if not stock:
        return jsonify({"error": "Not found"}), 404

    # Extract the stock's symbol and number of shares
    symbol = stock['symbol']
    shares = stock['shares']

    # Define the API URL and headers
    api_url = f'https://api.api-ninjas.com/v1/stockprice?ticker={symbol}'
    headers = {
        'X-Api-Key': API_KEY  # API key for external stock price service
    }

    try:
        # Make the GET request to the external API
        response = requests.get(api_url, headers=headers, timeout=5)

        # Check if the API request was successful
        if response.status_code != 200:
            return jsonify({"server error": f"API response code {response.status_code}"}), 500

        # Parse the API response
        data = response.json()
        if not data or "price" not in data:
            return jsonify({"server error": "No price data returned from API"}), 500

        # Extract the stock price (ticker) from the response
        ticker = data['price']

        # Calculate the stock's value in the portfolio
        stock_value = ticker * shares
        stock_value = round(stock_value, 2)  # Round to two decimal places

        # Return the response in the required format
        return jsonify({
            "symbol": symbol,
            "ticker": round(ticker, 2),
            "stock value": stock_value
        }), 200

    except requests.exceptions.RequestException as e:
        # Handle any request exceptions (e.g., network errors)
        return jsonify({"server error": str(e)}), 500

@app.route('/portfolio-value', methods=['GET'])
def get_portfolio_value():
    # Initialize total portfolio value
    portfolio_total = 0.0
    today_date = datetime.now().strftime('%d-%m-%Y')  # Get the current date in DD-MM-YYYY format

    headers = {
        'X-Api-Key': API_KEY  # API key for external stock price service
    }

    try:
        # Retrieve all stocks from MongoDB
        stocks = list(collection.find({}, {'_id': 0}))

        # Loop through each stock in the portfolio and calculate its value
        for stock in stocks:
            symbol = stock['symbol']
            shares = stock['shares']

            # Make a request to the external API to get the current stock price (ticker)
            api_url = f'https://api.api-ninjas.com/v1/stockprice?ticker={symbol}'
            response = requests.get(api_url, headers=headers, timeout=5)

            # Check if the request was successful
            if response.status_code != 200:
                return jsonify({"server error": f"API response code {response.status_code}"}), 500

            # Parse the response
            data = response.json()
            if not data or "price" not in data:
                return jsonify({"server error": "No price data returned from API"}), 500

            # Extract the ticker price and calculate stock value
            ticker = data['price']
            stock_value = ticker * shares
            portfolio_total += stock_value  # Add the stock value to the total portfolio value

        # Round the total portfolio value to two decimal places
        portfolio_total = round(portfolio_total, 2)

        # Return the portfolio value in the required format
        return jsonify({
            "date": today_date,
            "portfolio value": portfolio_total
        }), 200

    except requests.exceptions.RequestException as e:
        # Handle any exceptions during the API request (e.g., network issues)
        return jsonify({"server error": str(e)}), 500

@app.route('/kill', methods=['GET'])
def kill_container():
 os._exit(1)

# Run the Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)