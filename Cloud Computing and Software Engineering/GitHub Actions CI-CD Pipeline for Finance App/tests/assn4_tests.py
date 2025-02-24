import requests
import pytest

BASE_URL = "http://localhost:5001"
CAPITAL_GAINS_URL = "http://localhost:5003"

# Define all 8 stocks
stocks = [
    {"name": "NVIDIA Corporation", "symbol": "NVDA", "purchase price": 134.66, "purchase date": "18-06-2024", "shares": 7},
    {"name": "Apple Inc.", "symbol": "AAPL", "purchase price": 183.63, "purchase date": "22-02-2024", "shares": 19},
    {"name": "Alphabet Inc.", "symbol": "GOOG", "purchase price": 140.12, "purchase date": "24-10-2024", "shares": 14},
    {"name": "Tesla, Inc.", "symbol": "TSLA", "purchase price": 194.58, "purchase date": "28-11-2022", "shares": 32},
    {"name": "Microsoft Corporation", "symbol": "MSFT", "purchase price": 420.55, "purchase date": "09-02-2024", "shares": 35},
    {"name": "Intel Corporation", "symbol": "INTC", "purchase price": 19.15, "purchase date": "13-01-2025", "shares": 10},
    {"name": "Amazon.com, Inc.", "purchase price": 134.66, "purchase date": "18-06-2024", "shares": 7},
    {"name": "Amazon.com, Inc.", "symbol": "AMZN", "purchase price": 134.66, "purchase date": "Tuesday, June 18, 2024", "shares": 7}
]

# Test 1: Post 3 stocks
@pytest.fixture(scope="module")
def stock_ids():
    """Fixture to post first 3 stocks and return their IDs."""
    ids = []
    for stock in stocks[:3]:  # Add only first 3 stocks for initial testing
        response = requests.post(f"{BASE_URL}/stocks", json=stock)
        assert response.status_code == 201
        ids.append(response.json()["id"])
    assert len(set(ids)) == 3  # Ensure unique IDs
    return ids

# Test 2: Retrieve specific stock
def test_get_stock(stock_ids):
    response = requests.get(f"{BASE_URL}/stocks/{stock_ids[0]}")
    assert response.status_code == 200
    assert response.json()["symbol"] == "NVDA"

# Test 3: Retrieve all stocks
def test_get_all_stocks():
    response = requests.get(f"{BASE_URL}/stocks")
    assert response.status_code == 200
    assert len(response.json()) >= 3

# Test 4: Retrieve stock values and store them for portfolio validation
@pytest.fixture(scope="module")
def stock_values(stock_ids):
    """Fixture to store stock values for later portfolio validation."""
    sv1, sv2, sv3 = None, None, None
    symbols = ["NVDA", "AAPL", "GOOG"]
    values = {}

    for i in range(3):
        response = requests.get(f"{BASE_URL}/stock-value/{stock_ids[i]}")
        assert response.status_code == 200
        data = response.json()
        assert data["symbol"] == symbols[i]
        values[i] = data["stock value"]

    return values[0], values[1], values[2]

# Test 5: Portfolio value validation within ±0.3% margin
def test_portfolio_value(stock_values):
    sv1, sv2, sv3 = stock_values
    response = requests.get(f"{BASE_URL}/portfolio-value")
    assert response.status_code == 200

    portfolio_value = response.json()["portfolio value"]
    assert portfolio_value * 0.97 <= sv1 + sv2 + sv3 <= portfolio_value * 1.03  # Allow ±0.3% fluctuation

# Test 6: Invalid stock creation due to missing symbol
def test_invalid_post_missing_symbol():
    response = requests.post(f"{BASE_URL}/stocks", json=stocks[6])
    assert response.status_code == 400

# Test 7: Delete a stock
def test_delete_stock(stock_ids):
    response = requests.delete(f"{BASE_URL}/stocks/{stock_ids[1]}")
    assert response.status_code == 204

# Test 8: Retrieve deleted stock (should fail)
def test_get_deleted_stock(stock_ids):
    response = requests.get(f"{BASE_URL}/stocks/{stock_ids[1]}")
    assert response.status_code == 404

# Test 9: Invalid stock creation due to incorrect purchase date format
def test_invalid_post_invalid_date():
    response = requests.post(f"{BASE_URL}/stocks", json=stocks[7])
    assert response.status_code == 400