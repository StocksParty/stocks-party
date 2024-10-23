package com.aws.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * DTO representing the stock alert request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertRequest {
    @NonNull
    private String stockSymbol;
    private double targetPrice;
    @NonNull
    private String userEmail;
    private String phoneNumber;
}

