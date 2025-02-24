import requests
import json

BASE_URLS = {
    "stocks": "http://localhost:5001",
    "capital-gains": "http://localhost:5003"
}

# Stocks to be added before queries
stocks = [
    {"name": "NVIDIA Corporation", "symbol": "NVDA", "purchase price": 134.66, "purchase date": "18-06-2024", "shares": 7},
    {"name": "Apple Inc.", "symbol": "AAPL", "purchase price": 183.63, "purchase date": "22-02-2024", "shares": 19},
    {"name": "Alphabet Inc.", "symbol": "GOOG", "purchase price": 140.12, "purchase date": "24-10-2024", "shares": 14},
    {"name": "Tesla, Inc.", "symbol": "TSLA", "purchase price": 194.58, "purchase date": "28-11-2022", "shares": 32},
    {"name": "Microsoft Corporation", "symbol": "MSFT", "purchase price": 420.55, "purchase date": "09-02-2024", "shares": 35},
    {"name": "Intel Corporation", "symbol": "INTC", "purchase price": 19.15, "purchase date": "13-01-2025", "shares": 10}
]

# Post initial stocks
for stock in stocks:
    response = requests.post(f"{BASE_URLS['stocks']}/stocks", json=stock)
    print(f"Posted stock: {response.text}")

# Read queries from query.txt
with open("query.txt", "r") as f:
    queries = f.readlines()

responses = []
for query in queries:
    service, query_string = query.strip().split(":")
    url = f"{BASE_URLS[service]}/{service}?{query_string}"
    res = requests.get(url)
    responses.append(f"query: {query}response: {res.text}\n")

# Save responses to response.txt
with open("response.txt", "w") as f:
    f.writelines(responses)