package com.aws.project.service.stock;

import com.aws.project.DTO.StockPriceResponse;
import com.aws.project.DTO.HistoricalStockPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for fetching and processing stock price data from an external API.
 */
@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final RestTemplate restTemplate;

    @Value("${stock.api.key}")
    private String apiKey;

    @Value("${stock.api.url}")
    private String apiUrl;


    /**
     * Fetches the current stock price for the given symbol, interval, and output size.
     * If any of these parameters are null, default values are applied.
     *
     * @param symbol     the stock symbol (e.g., "AAPL"). Default is "IBM" if null.
     * @param interval   the time interval between stock prices (e.g., "5min"). Default is "5min" if null.
     * @param outputSize the size of the output data (e.g., "compact" or "full"). Default is "compact" if null.
     * @return a {@link StockPriceResponse} object containing stock price details.
     * @throws RuntimeException if the stock price data cannot be fetched or is invalid.
     */
    public StockPriceResponse getCurrentStockPrice(String symbol, String interval, String outputSize) {
        // Set default values for symbol, interval, and outputSize
        symbol = getOrDefault(symbol, "IBM");
        interval = getOrDefault(interval, "5min");
        outputSize = getOrDefault(outputSize, "compact");

        // Construct the request URL
        String url = buildRequestUrl(symbol, interval, outputSize);

        // Call the API and get the response
        Map<String, Object> response = fetchStockData(url);

        // Parse and return the stock price response
        return parseStockPriceResponse(response, symbol, interval, outputSize);
    }

    /**
     * Gets the latest price for the stock symbol
     * @param symbol stock symbol
     * @return latest price
     */
    public double getLatestPrice(String symbol) {
        StockPriceResponse stockPriceResponse = getCurrentStockPrice(symbol, "1min", "compact");
        return stockPriceResponse.getCurrentPrice();
    }

    /**
     * Returns the provided value or a default value if the provided value is null.
     */
    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Constructs the API request URL for fetching stock price data.
     *
     * @param symbol     the stock symbol (e.g., "AAPL").
     * @param interval   the time interval between stock prices (e.g., "5min").
     * @param outputSize the size of the output data (e.g., "compact" or "full").
     * @return a properly formatted API request URL as a string.
     */
    private String buildRequestUrl(String symbol, String interval, String outputSize) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("function", "TIME_SERIES_INTRADAY")
                .queryParam("symbol", symbol)
                .queryParam("interval", interval)
                .queryParam("outputsize", outputSize)
                .queryParam("apikey", apiKey)
                .toUriString();
    }

    /**
     * Sends an HTTP GET request to fetch stock data from the external API.
     *
     * @param url the API request URL.
     * @return a map containing the stock data response.
     * @throws RuntimeException if the API response is null or empty.
     */
    private Map<String, Object> fetchStockData(String url) {
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            throw new RuntimeException("Failed to fetch stock data from the external API.");
        }
        return response;
    }

    /**
     * Parses the API response and extracts relevant stock price data.
     *
     * @param response   the raw API response as a map.
     * @param symbol     the stock symbol.
     * @param interval   the time interval between stock prices.
     * @param outputSize the size of the output data (compact or full).
     * @return a {@link StockPriceResponse} object with the parsed stock price data.
     * @throws RuntimeException if the response does not contain valid stock price data.
     */
    private StockPriceResponse parseStockPriceResponse(Map<String, Object> response, String symbol, String interval, String outputSize) {
        if (!response.containsKey("Time Series (" + interval + ")")) {
            throw new RuntimeException("No time series data available for symbol: " + symbol);
        }

        Map<String, Map<String, String>> timeSeries = (Map<String, Map<String, String>>) response.get("Time Series (" + interval + ")");
        if (timeSeries == null || timeSeries.isEmpty()) {
            throw new RuntimeException("No time series data found for symbol: " + symbol);
        }

        String latestTimestamp = timeSeries.keySet().iterator().next();
        Map<String, String> stockData = timeSeries.get(latestTimestamp);

        StockPriceResponse stockPriceResponse = extractStockPriceData(symbol, latestTimestamp, stockData);

        // If output size is full, add historical data
        if ("full".equalsIgnoreCase(outputSize)) {
            List<HistoricalStockPrice> historicalPrices = extractHistoricalStockPrices(timeSeries);
            stockPriceResponse.setHistoricalData(historicalPrices);
        }

        return stockPriceResponse;
    }

    /**
     * Extracts the stock price details from the stock data and returns a {@link StockPriceResponse} object.
     *
     * @param symbol          the stock symbol.
     * @param latestTimestamp the latest timestamp of the stock data.
     * @param stockData       the stock data map containing price details.
     * @return a {@link StockPriceResponse} object containing the current, open, high, and low prices.
     * @throws RuntimeException if the stock data is incomplete or invalid.
     */
    private StockPriceResponse extractStockPriceData(String symbol, String latestTimestamp, Map<String, String> stockData) {
        if (stockData == null || stockData.get("4. close") == null) {
            throw new RuntimeException("Incomplete stock data for symbol: " + symbol);
        }

        double currentPrice = Double.parseDouble(stockData.get("4. close"));
        double openPrice = stockData.get("1. open") != null ? Double.parseDouble(stockData.get("1. open")) : 0.0;
        double highPrice = stockData.get("2. high") != null ? Double.parseDouble(stockData.get("2. high")) : 0.0;
        double lowPrice = stockData.get("3. low") != null ? Double.parseDouble(stockData.get("3. low")) : 0.0;

        return new StockPriceResponse(symbol, currentPrice, latestTimestamp, openPrice, highPrice, lowPrice);
    }

    /**
     * Extracts historical stock prices for full output size.
     *
     * @param timeSeries the time series data map.
     * @return a list of {@link HistoricalStockPrice} objects containing historical price data.
     */
    private List<HistoricalStockPrice> extractHistoricalStockPrices(Map<String, Map<String, String>> timeSeries) {
        List<HistoricalStockPrice> historicalPrices = new ArrayList<>();

        // Iterate over the entire time series and extract historical data
        for (Map.Entry<String, Map<String, String>> entry : timeSeries.entrySet()) {
            String timestamp = entry.getKey();
            Map<String, String> stockData = entry.getValue();

            double openPrice = stockData.get("1. open") != null ? Double.parseDouble(stockData.get("1. open")) : 0.0;
            double highPrice = stockData.get("2. high") != null ? Double.parseDouble(stockData.get("2. high")) : 0.0;
            double lowPrice = stockData.get("3. low") != null ? Double.parseDouble(stockData.get("3. low")) : 0.0;
            double closePrice = stockData.get("4. close") != null ? Double.parseDouble(stockData.get("4. close")) : 0.0;

            historicalPrices.add(new HistoricalStockPrice(timestamp, openPrice, highPrice, lowPrice, closePrice));
        }

        return historicalPrices;
    }
}
