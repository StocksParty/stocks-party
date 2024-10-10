package com.aws.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing historical stock price data for a specific timestamp.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalStockPrice {

    // The timestamp for this stock data point (e.g., "2024-10-10 15:30:00")
    private String timestamp;

    // The opening price at the given timestamp
    private double openPrice;

    // The highest price at the given timestamp
    private double highPrice;

    // The lowest price at the given timestamp
    private double lowPrice;

    // The closing price at the given timestamp
    private double closePrice;
}
