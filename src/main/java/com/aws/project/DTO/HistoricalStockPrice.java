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

    // The timestamp
    private String timestamp;

    // The opening price
    private double openPrice;

    // The highest price
    private double highPrice;

    // The lowest price
    private double lowPrice;

    // The closing price
    private double closePrice;
}
