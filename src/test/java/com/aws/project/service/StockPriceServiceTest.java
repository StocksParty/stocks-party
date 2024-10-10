package com.aws.project.service;

import com.aws.project.DTO.StockPriceResponse;
import com.aws.project.service.stock.StockPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class StockPriceServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StockPriceService stockPriceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the apiUrl and apiKey for tests
        ReflectionTestUtils.setField(stockPriceService, "apiUrl", "https://mock-api-url.com");
        ReflectionTestUtils.setField(stockPriceService, "apiKey", "mockApiKey");
    }

    @Test
    public void testGetCurrentStockPrice_withDefaults() {
        // Mock the API response for a compact response
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, String> stockData = new HashMap<>();
        stockData.put("4. close", "150.0");
        stockData.put("1. open", "145.0");
        stockData.put("2. high", "151.0");
        stockData.put("3. low", "144.0");
        Map<String, Map<String, String>> timeSeries = new HashMap<>();
        timeSeries.put("2024-10-10 15:30:00", stockData);
        mockResponse.put("Time Series (5min)", timeSeries);

        // Mocking RestTemplate call
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        // Call the service method with no symbol or interval (defaults should apply)
        StockPriceResponse response = stockPriceService.getCurrentStockPrice(null, null, null);

        // Validate the response
        assertNotNull(response);
        assertNotNull(response.getSymbol());
        assertNotNull(response.getCurrentPrice());
        assertNotNull(response.getOpenPrice());
        assertNotNull(response.getHighPrice());
        assertNotNull(response.getLowPrice());
        assertNotNull(response.getTimestamp());

        // Assert default symbol is IBM and interval is 5min
        assertEquals("IBM", response.getSymbol());
        assertEquals(150.0, response.getCurrentPrice());
        assertEquals(145.0, response.getOpenPrice());
        assertEquals(151.0, response.getHighPrice());
        assertEquals(144.0, response.getLowPrice());
        assertEquals("2024-10-10 15:30:00", response.getTimestamp());

        // Assert that historical data is null (compact response)
        assertNull(response.getHistoricalData());
    }

    @Test
    public void testGetCurrentStockPrice_withCustomParams() {
        // Mock the API response for a compact response
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, String> stockData = new HashMap<>();
        stockData.put("4. close", "180.0");
        stockData.put("1. open", "175.0");
        stockData.put("2. high", "182.0");
        stockData.put("3. low", "170.0");
        Map<String, Map<String, String>> timeSeries = new HashMap<>();
        timeSeries.put("2024-10-10 15:30:00", stockData);
        mockResponse.put("Time Series (15min)", timeSeries);

        // Mocking RestTemplate call
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        // Call the service method with custom symbol and interval
        StockPriceResponse response = stockPriceService.getCurrentStockPrice("AAPL", "15min", "compact");

        // Validate the response
        assertNotNull(response);
        assertNotNull(response.getSymbol());
        assertNotNull(response.getCurrentPrice());
        assertNotNull(response.getOpenPrice());
        assertNotNull(response.getHighPrice());
        assertNotNull(response.getLowPrice());
        assertNotNull(response.getTimestamp());

        // Assert custom symbol and interval
        assertEquals("AAPL", response.getSymbol());
        assertEquals(180.0, response.getCurrentPrice());
        assertEquals(175.0, response.getOpenPrice());
        assertEquals(182.0, response.getHighPrice());
        assertEquals(170.0, response.getLowPrice());
        assertEquals("2024-10-10 15:30:00", response.getTimestamp());

        // Assert that historical data is null (compact response)
        assertNull(response.getHistoricalData());
    }

    @Test
    public void testGetCurrentStockPrice_withFullResponse() {
        // Mock the API response for a full response (with historical data)
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, String> stockData = new HashMap<>();
        stockData.put("4. close", "180.0");
        stockData.put("1. open", "175.0");
        stockData.put("2. high", "182.0");
        stockData.put("3. low", "170.0");
        Map<String, Map<String, String>> timeSeries = new HashMap<>();
        timeSeries.put("2024-10-10 15:30:00", stockData);

        // Historical data for a second timestamp
        Map<String, String> historicalStockData = new HashMap<>();
        historicalStockData.put("4. close", "178.0");
        historicalStockData.put("1. open", "173.0");
        historicalStockData.put("2. high", "180.0");
        historicalStockData.put("3. low", "170.0");
        timeSeries.put("2024-10-10 14:30:00", historicalStockData);

        mockResponse.put("Time Series (15min)", timeSeries);

        // Mocking RestTemplate call
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        // Call the service method with custom symbol and interval and full response
        StockPriceResponse response = stockPriceService.getCurrentStockPrice("AAPL", "15min", "full");

        // Validate the response
        assertNotNull(response);
        assertNotNull(response.getSymbol());
        assertNotNull(response.getCurrentPrice());
        assertNotNull(response.getOpenPrice());
        assertNotNull(response.getHighPrice());
        assertNotNull(response.getLowPrice());
        assertNotNull(response.getTimestamp());

        // Assert that historical data is not null and contains two entries
        assertNotNull(response.getHistoricalData());
        assertEquals(2, response.getHistoricalData().size());
    }

    @Test
    public void testGetCurrentStockPrice_NoData() {
        // Mock empty API response
        Map<String, Object> mockResponse = new HashMap<>();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        // Call the service and expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stockPriceService.getCurrentStockPrice("AAPL", "5min", "compact");
        });

        assertEquals("No time series data available for symbol: AAPL", exception.getMessage());
    }

    @Test
    public void testGetCurrentStockPrice_IncompleteData() {
        // Mock the API response with missing "4. close" field
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, String> stockData = new HashMap<>();
        stockData.put("1. open", "145.0");
        stockData.put("2. high", "151.0");
        stockData.put("3. low", "144.0");
        Map<String, Map<String, String>> timeSeries = new HashMap<>();
        timeSeries.put("2024-10-10 15:30:00", stockData);
        mockResponse.put("Time Series (5min)", timeSeries);

        // Mocking RestTemplate call
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        // Call the service and expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stockPriceService.getCurrentStockPrice("AAPL", "5min", "compact");
        });

        assertEquals("Incomplete stock data for symbol: AAPL", exception.getMessage());
    }
}
