package com.aws.project.controller;

import com.aws.project.DTO.StockAlertRequest;
import com.aws.project.DTO.StockPriceResponse;
import com.aws.project.base.ApiResponse;
import com.aws.project.service.DynamoDbService;
import com.aws.project.service.stock.StockAlertService;
import com.aws.project.service.stock.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing stock prices and alerts.
 * Provides endpoints to fetch stock prices, create and delete alerts,
 * and check stock prices to send notifications if a target price is met.
 */
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockAlertController {

    private final StockPriceService stockPriceService;
    private final StockAlertService stockAlertService;
    private final DynamoDbService dynamoDbService;

    /**
     * Endpoint for fetching the current stock price.
     *
     * @param symbol     the stock symbol (e.g., "AAPL"). Defaults to "IBM" if not provided.
     * @param interval   the time interval between stock prices (e.g., "5min"). Defaults to "5min" if not provided.
     * @param outputsize the output size of the data ("compact" or "full"). Defaults to "compact".
     * @return a {@link ResponseEntity} containing an {@link ApiResponse<StockPriceResponse>} with the stock price information.
     */
    @GetMapping("/price")
    public ResponseEntity<ApiResponse<StockPriceResponse>> getStockPrice(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String interval,
            @RequestParam(defaultValue = "compact") String outputsize) {

        try {
            // Call the service with symbol, interval, and outputsize
            StockPriceResponse stockPriceResponse = stockPriceService.getCurrentStockPrice(
                    symbol != null ? symbol : "IBM",
                    interval != null ? interval : "5min",
                    outputsize);

            return ResponseEntity.ok(new ApiResponse<StockPriceResponse>(true, "Stock price fetched successfully", stockPriceResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<StockPriceResponse>(false, "Failed to fetch stock price."));
        }
    }

    /**
     * Endpoint for fetching the current stock price alerts for an email.
     */
    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllAlertsByEmail(@RequestParam String email) {
        try {
            List<Map<String, Object>> alerts = stockAlertService.getAllAlertsForEmail(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts fetched successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to fetch alerts"));
        }
    }

    /**
     * Endpoint for creating a new stock alert.
     *
     * @param alertRequest the {@link StockAlertRequest} containing the stock symbol, target price, and email for notification.
     * @return a {@link ResponseEntity} containing an {@link ApiResponse<String>} indicating the success or failure of alert creation.
     */
    @PostMapping("/alert")
    public ResponseEntity<ApiResponse<String>> createAlert(@RequestBody StockAlertRequest alertRequest) {
        try {
            stockAlertService.createAlert(alertRequest);
            return ResponseEntity.ok(new ApiResponse<String>(true, "Alert created successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(false, "Failed to create alert."));
        }
    }

    /**
     * Endpoint for deleting an existing stock alert.
     *
     * @param symbol the stock symbol for which the alert was created.
     * @param email  the email address associated with the alert.
     * @return a {@link ResponseEntity} containing an {@link ApiResponse<String>} indicating the success or failure of alert deletion.
     */
    @DeleteMapping("/alert")
    public ResponseEntity<ApiResponse<String>> deleteAlert(
            @RequestParam String symbol,
            @RequestParam String email) {
        try {
            stockAlertService.deleteAlert(symbol, email);
            return ResponseEntity.ok(new ApiResponse<String>(true, "Alert deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(false, "Failed to delete alert."));
        }
    }
}
