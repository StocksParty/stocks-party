package com.aws.project.service;

import com.aws.project.DTO.StockAlertRequest;
import com.aws.project.DTO.StockPriceResponse;
import com.aws.project.service.stock.StockAlertService;
import com.aws.project.service.stock.StockPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StockAlertServiceTest {

    @Mock
    private DynamoDbService dynamoDbService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private StockPriceService stockPriceService;

    @InjectMocks
    private StockAlertService stockAlertService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAlertSuccess() {
        // Now using the @AllArgsConstructor generated by Lombok
        StockAlertRequest alertRequest = new StockAlertRequest("AAPL", 150.0, "user@example.com", "9999999999");
        stockAlertService.createAlert(alertRequest);
        verify(dynamoDbService, times(1)).saveAlert(anyString(), anyDouble(), anyString());
    }

    @Test
    public void testCheckStockPriceAndNotify_Success() {
        // Mock the alert from DynamoDB
        Map<String, AttributeValue> mockAlert = Map.of(
                "symbol", AttributeValue.builder().s("AAPL").build(),
                "targetPrice", AttributeValue.builder().n("150.0").build()
        );
        when(dynamoDbService.getAlert(anyString(), anyString())).thenReturn(mockAlert);

        // Mock the stock price response with additional fields (openPrice, highPrice, lowPrice)
        // Set currentPrice to 160.0, which is greater than the targetPrice (150.0)
        StockPriceResponse mockStockPriceResponse = new StockPriceResponse("AAPL", 160.0, "2024-01-05T15:30:00Z", 135.0, 145.0, 130.0);

        // Provide the required 3 arguments for getCurrentStockPrice
        when(stockPriceService.getCurrentStockPrice(anyString(), anyString(), anyString()))
                .thenReturn(mockStockPriceResponse);

        stockAlertService.checkAllAlerts();

        // Verify SNS service is called since the current price is greater than the target price
//        verify(snsService, times(1)).sendEmailNotification(anyString());
    }


    @Test
    public void testCheckStockPriceAndNotify_PriceBelowTarget() {
        // Mock the alert from DynamoDB
        Map<String, AttributeValue> mockAlert = Map.of(
                "symbol", AttributeValue.builder().s("AAPL").build(),
                "targetPrice", AttributeValue.builder().n("150.0").build()
        );
        when(dynamoDbService.getAlert(anyString(), anyString())).thenReturn(mockAlert);

        // Mock the stock price response with additional fields (openPrice, highPrice, lowPrice)
        StockPriceResponse mockStockPriceResponse = new StockPriceResponse("AAPL", 140.0, "2024-01-05T15:30:00Z", 135.0, 145.0, 130.0);

        // Provide the required 3 arguments for getCurrentStockPrice
        when(stockPriceService.getCurrentStockPrice(anyString(), anyString(), anyString()))
                .thenReturn(mockStockPriceResponse);

        stockAlertService.checkAllAlerts();

        // Verify SNS service is not called since the price is below target
//        verify(snsService, times(0)).sendEmailNotification(anyString());
    }

//    @Test
//    public void testCheckStockPriceAndNotify_NoAlertFound() {
//        // Mock DynamoDB returning no alert
//        when(dynamoDbService.getAlert(anyString(), anyString())).thenReturn(null);
//
//        // Test that an exception is thrown when no alert is found
//        assertThrows(RuntimeException.class, () -> stockAlertService.checkAllAlerts());
//    }

    @Test
    public void testDeleteAlertSuccess() {
        stockAlertService.deleteAlert("AAPL", "user@example.com");
        verify(dynamoDbService, times(1)).deleteAlert(anyString(), anyString());
    }
}
