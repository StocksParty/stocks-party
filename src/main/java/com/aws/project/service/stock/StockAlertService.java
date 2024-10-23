package com.aws.project.service.stock;

import com.aws.project.DTO.StockAlertRequest;
import com.aws.project.service.DynamoDbService;
import com.aws.project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

/**
 * Service for managing stock price alerts, checking stock prices, and sending notifications.
 */
@Service
@RequiredArgsConstructor
public class StockAlertService {

    private final DynamoDbService dynamoDbService;
    private final NotificationService notificationService;
    private final StockPriceService stockPriceService;

    /**
     * Creates a new stock price alert.
     *
     * @param alertRequest Request containing stock symbol, target price, and user email.
     */
    public void createAlert(StockAlertRequest alertRequest) {
        dynamoDbService.saveAlert(alertRequest.getStockSymbol(), alertRequest.getTargetPrice(), alertRequest.getUserEmail());
        notificationService.subscribeEmailToTopic(alertRequest.getUserEmail());
    }

    /**
     * Returns a list full of stock alerts based on the email provided.
     *
     * @param email Request containing user email.
     */
    public List<Map<String, Object>> getAllAlertsForEmail(String email) {
        return dynamoDbService.getAllAlertsByEmail(email);
    }


    /**
     * Will run automatically twice a day
     * 10am and 3pm
     * Checks the current stock price against the user's target price and sends a notification if applicable.
     */
    @Scheduled(cron = "0 0 10,15 * * *")
    public void checkAllAlerts() {
        // Fetch all alerts from DynamoDB
        List<Map<String, AttributeValue>> alerts = dynamoDbService.getAllAlerts();

        for (Map<String, AttributeValue> alert : alerts) {
            // Convert AttributeValue to regular String and Double
            String symbol = alert.get("symbol").s();
            double targetPrice = Double.parseDouble(alert.get("targetPrice").n());
            String email = alert.get("email").s();
            String phone = alert.get("phoneNumber").s();

            // Extract the current price from the StockPriceResponse
            double currentPrice = stockPriceService.getLatestPrice(symbol);

            // Compare the current price with the target price
            if (currentPrice >= targetPrice) {
                // Send notification to the user
                if (email !=null && !email.isEmpty()) {
                    notificationService.sendPriceAlertBySNS(email, phone, symbol, currentPrice);
                    notificationService.sendPriceAlertByEmail(email, symbol, currentPrice);
                }
                if (phone != null && !phone.isEmpty()) {
                    notificationService.sendPriceAlertBySNS(email, phone, symbol, currentPrice);
                }
            }
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
