package com.aws.project.controller;

import com.aws.project.base.ApiResponse;
import com.aws.project.service.NotificationService;
import com.aws.project.service.stock.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationService notificationService;

    /**
     * Endpoint to test the notification system.
     * This will fetch the current stock price and compare it with the target price.
     * If the current price meets or exceeds the target price, a notification will be sent.
     *
     * @param email        the email to receive the notification.
     * @param stockSymbol  the stock symbol to check.
     * @param targetPrice  the target price to compare with.
     * @return ResponseEntity with true or false, indicating whether a notification was sent.
     */
    @GetMapping("/notification")
    public ResponseEntity<ApiResponse<Boolean>> testNotification(
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String stockSymbol,
            @RequestParam double targetPrice) {

        try {
            // Hard Coded values for testing purpose
            // Not wasting api calls for this test
            email = "nicholasbotorog@gmail.com";
            stockSymbol = "PLTR";
            targetPrice = 42.83;
            var currentPrice = 41.83;
            if (currentPrice >= targetPrice) {
                notificationService.sendPriceAlertBySNS(email, phone, stockSymbol, currentPrice);
                return ResponseEntity.ok(new ApiResponse<>(true, "Notification sent.", true));
            } else {
                notificationService.sendTestNotification(email, phone, stockSymbol, currentPrice);
                return ResponseEntity.ok(new ApiResponse<>(false, "Notification not needed.", false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error occurred during notification test.", null));
        }
    }
}
