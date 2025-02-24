import os
from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

# Define a single stock service URL
STOCKS_URL = os.getenv('STOCKS_URL', 'http://stocks:8000/stocks')

# Function to fetch stock data
def fetch_stocks():
    """Fetch stocks from the stocks service."""
    try:
        response = requests.get(STOCKS_URL, timeout=5)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"Failed to fetch stocks: {str(e)}"}), 500

# Function to calculate capital gains
def calculate_capital_gains(stocks, purchase_price_key="purchase price", shares_key="shares", current_price_key="ticker"):
    """Calculate capital gains based on stock data."""
    capital_gains = 0.0
    for stock in stocks:
        cp_one = stock.get(current_price_key, 0) - stock.get(purchase_price_key, 0)
        capital_gains += cp_one * stock.get(shares_key, 0)
    return capital_gains

@app.route('/capital-gains', methods=['GET'])
def get_capital_gains():
    """Fetch stock data, filter, retrieve current prices, and calculate capital gains."""
    numsharesgt = request.args.get('numsharesgt', type=int)
    numshareslt = request.args.get('numshareslt', type=int)

    # Fetch stocks from the stock service
    stocks = fetch_stocks()
    if not isinstance(stocks, list):
        return stocks  # Return error response if fetch_stocks failed

    # Apply filters
    filtered_stocks = [stock for stock in stocks if (
        (numsharesgt is None or stock.get('shares', 0) > numsharesgt) and
        (numshareslt is None or stock.get('shares', 0) < numshareslt)
    )]

    # Fetch current prices for each stock
    for stock in filtered_stocks:
        try:
            api_url = f"https://api.api-ninjas.com/v1/stockprice?ticker={stock['symbol']}"
            headers = {'X-Api-Key': os.getenv('API_KEY')}
            response = requests.get(api_url, headers=headers, timeout=5)
            if response.status_code == 200:
                stock_price_data = response.json()
                stock['ticker'] = stock_price_data.get('price', 0)
            else:
                stock['ticker'] = 0
        except requests.exceptions.RequestException:
            stock['ticker'] = 0

    # Calculate capital gains
    total_capital_gains = calculate_capital_gains(filtered_stocks)
    return str(round(total_capital_gains, 2)), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)