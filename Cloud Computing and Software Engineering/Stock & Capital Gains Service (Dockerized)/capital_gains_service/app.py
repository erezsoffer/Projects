import os
from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

# Stocks services base URLs (configured via environment variables or default values)
STOCKS1_URL = os.getenv('STOCKS1_URL', 'http://stocks1-a:8000/stocks')
STOCKS2_URL = os.getenv('STOCKS2_URL', 'http://stocks2:8000/stocks')

# Helper functions
def fetch_stocks(base_url):
    try:
        response = requests.get(base_url, timeout=5)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"Failed to fetch stocks from {base_url}: {str(e)}"}), 500

def calculate_capital_gains(stocks, purchase_price_key="purchase price", shares_key="shares", current_price_key="ticker"):
    capital_gains = 0.0
    for stock in stocks:
        cp_one = stock.get(current_price_key, 0) - stock.get(purchase_price_key, 0)
        capital_gains += cp_one * stock.get(shares_key, 0)
    return capital_gains

@app.route('/capital-gains', methods=['GET'])
def get_capital_gains():
    query_params = request.args
    portfolio = query_params.get('portfolio')
    numsharesgt = query_params.get('numsharesgt', type=int)
    numshareslt = query_params.get('numshareslt', type=int)

    # Fetch stocks based on the portfolio parameter
    if portfolio == 'stocks1':
        stocks = fetch_stocks(STOCKS1_URL)
    elif portfolio == 'stocks2':
        stocks = fetch_stocks(STOCKS2_URL)
    else:
        stocks1 = fetch_stocks(STOCKS1_URL)
        stocks2 = fetch_stocks(STOCKS2_URL)
        if isinstance(stocks1, list) and isinstance(stocks2, list):
            stocks = stocks1 + stocks2
        else:
            return jsonify({"error": "Failed to fetch stocks from one or both services."}), 500

    if not isinstance(stocks, list):
        return stocks  # Error response from fetch_stocks

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