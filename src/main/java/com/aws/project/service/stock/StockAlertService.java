package com.aws.project.service.stock;

import com.aws.project.DTO.StockAlertRequest;
import com.aws.project.DTO.StockPriceResponse;
import com.aws.project.service.DynamoDbService;
import com.aws.project.service.SnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

/**
 * Service for managing stock price alerts, checking stock prices, and sending notifications.
 */
@Service
@RequiredArgsConstructor
public class StockAlertService {

    private final DynamoDbService dynamoDbService;
    private final SnsService snsService;
    private final StockPriceService stockPriceService;

    /**
     * Creates a new stock price alert.
     *
     * @param alertRequest Request containing stock symbol, target price, and user email.
     */
    public void createAlert(StockAlertRequest alertRequest) {
        dynamoDbService.saveAlert(alertRequest.getStockSymbol(), alertRequest.getTargetPrice(), alertRequest.getUserEmail());
    }


    /**
     * Checks the current stock price against the user's target price and sends a notification if applicable.
     *
     * @param symbol Stock symbol.
     * @param email  User's email.
     */
    public void checkStockPriceAndNotify(String symbol, String email) {
        Map<String, AttributeValue> alert = dynamoDbService.getAlert(symbol, email);
        if (alert == null) {
            throw new RuntimeException("No alert found for the given stock symbol and email.");
        }

        double targetPrice = Double.parseDouble(alert.get("targetPrice").n());

        // Pass the symbol along with default interval and outputsize to get the stock price
        StockPriceResponse stockPriceResponse = stockPriceService.getCurrentStockPrice(symbol, "5min", "compact");
        double currentPrice = stockPriceResponse.getCurrentPrice();

        if (currentPrice >= targetPrice) {
            String message = String.format("Stock %s has reached your target price of $%.2f. Current price: $%.2f", symbol, targetPrice, currentPrice);
            snsService.sendEmailNotification(message);
        }
    }


    /**
     * Deletes a stock price alert for a user.
     *
     * @param symbol Stock symbol.
     * @param email  User's email.
     */
    public void deleteAlert(String symbol, String email) {
        dynamoDbService.deleteAlert(symbol, email);
    }
}
