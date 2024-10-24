# Real-Time Stock Price Alert System

## Overview

The Real-Time Stock Price Alert System is a serverless application designed to track stock prices and notify users via email when a stock reaches a specified target price. The application fetches real-time stock data from an external API, allows users to create and manage stock alerts, and sends notifications when the target price is met.

[Not so much of a technical drawing](https://excalidraw.com)

## Features

- **Real-Time Stock Price Fetching**: Fetches current stock prices at specified intervals (e.g., 1min, 5min, etc.) using an external API.
- **Stock Alerts**: Users can create alerts for specific stocks, specifying a target price and an email for notifications.
- **Price Check and Notification**: The system checks the current stock price against the target price and sends an email notification if the price exceeds or meets the target.
- **REST API**: Exposes endpoints for stock price fetching, alert management (create, delete), and price checking.

## Tech Stack

- **Java 17**
- **Spring Boot 3.3.x**
- **AWS SDK (DynamoDB, SNS)** for database and notifications
- **Lombok** for reducing boilerplate code
- **RestTemplate** for API calls to the stock data provider
- **JUnit 5** and **Mockito** for unit testing

## API Endpoints

### 1. **Get Stock Price**

Fetches the current stock price for a specified stock symbol, interval, and output size.

- **URL**: `/stocks/price`
- **Method**: `GET`
- **Query Parameters**:
    - `symbol` (optional): The stock symbol (e.g., `AAPL`). Defaults to `IBM` if not provided.
    - `interval` (optional): The time interval between stock prices (e.g., `1min`, `5min`). Defaults to `5min`.
    - `outputsize` (optional): The size of the output data, either `compact` or `full`. Defaults to `compact`.

#### Example Request:

```bash
curl -X GET "https://0qcjw0wudl.execute-api.us-east-1.amazonaws.com/prod/stocks/price"

curl -X GET "https://0qcjw0wudl.execute-api.us-east-1.amazonaws.com/prod/stocks/price?symbol=PLTR&interval=60min&outputsize=full"

curl -X POST "https://0qcjw0wudl.execute-api.us-east-1.amazonaws.com/prod/stocks/alert" \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "AAPL",
    "targetPrice": 150.0,
    "email": "user@example.com"
  }'

curl -X DELETE "http://localhost:8080/stocks/alert?symbol=AAPL&email=user@example.com"

curl -X GET "https://0qcjw0wudl.execute-api.us-east-1.amazonaws.com/prod/stocks/alerts?email=test@test.com"
