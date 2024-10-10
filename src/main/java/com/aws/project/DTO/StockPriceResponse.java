package com.aws.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the stock price response.
 * It includes the current stock price and optional historical data when the output size is "full".
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceResponse {

    // The stock symbol (e.g., "AAPL")
    private String symbol;

    // The current stock price (e.g., "150.0")
    private double currentPrice;

    // The timestamp of the most recent price data (e.g., "2024-10-10 15:30:00")
    private String timestamp;

    // The opening price at the latest timestamp
    private double openPrice;

    // The highest price at the latest timestamp
    private double highPrice;

    // The lowest price at the latest timestamp
    private double lowPrice;

    // The historical stock price data (optional, included only if output size is "full")
    private List<HistoricalStockPrice> historicalData;

    // Constructor for when there is no historical data (compact response)
    public StockPriceResponse(String symbol, double currentPrice, String timestamp, double openPrice, double highPrice, double lowPrice) {
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.timestamp = timestamp;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
    }
}
