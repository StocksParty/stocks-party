package com.aws.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the stock alert request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertRequest {
    private String stockSymbol;
    private double targetPrice;
    private String userEmail;
}

