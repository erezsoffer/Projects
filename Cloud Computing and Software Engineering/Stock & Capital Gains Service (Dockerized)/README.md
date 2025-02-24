# Stock & Capital Gains Service (Dockerized)

### Overview

A multi-service finance application using Docker Compose, featuring:

- Two instances of a stock service for tracking buy/sell operations.
- A capital gains service to calculate profits using FIFO accounting.
- A MongoDB database for persistence.
- An NGINX reverse proxy for request routing and load balancing.
- Automatic recovery of services after failures.

### Technologies

- Flask (Backend API)
- MongoDB (Database)
- Docker & Docker Compose (Containerization & Orchestration)
- NGINX (Reverse Proxy & Load Balancing)

### Setup & Run

```sh
# Clone the repository
cd 'Stock & Capital Gains Service (Dockerized)'

# Run the application
docker-compose up --build
```
