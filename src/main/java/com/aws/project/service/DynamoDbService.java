package com.aws.project.service;

import com.aws.project.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Import(AppConfig.class)
public class DynamoDbService {

    private final DynamoDbClient dynamoDbClient;

    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    @Value("${aws.dynamodb.index-name}")
    private String indexName;

    //Fetches all the requests from the db
    public List<Map<String, AttributeValue>> getAllAlerts() {
        List<Map<String, AttributeValue>> alerts = new ArrayList<>();

        try {
            // Create the ScanRequest to retrieve all data
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .build();

            // Perform the scan operation
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

            // Add the items to the list
            alerts.addAll(scanResponse.items());

        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch alerts from DynamoDB. Error: " + e.getMessage());
        }

        return alerts;
    }

    // Save a new stock price alert to DynamoDB
    public void saveAlert(String stockSymbol, double targetPrice, String email) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("symbol", AttributeValue.builder().s(stockSymbol).build());
            item.put("email", AttributeValue.builder().s(email).build());
            item.put("targetPrice", AttributeValue.builder().n(String.valueOf(targetPrice)).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
            System.out.println("Successfully saved the alert.");
        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.err.println("Unable to save the alert. Error: " + e.getMessage());
        }
    }

    // Retrieve a stock price alert from DynamoDB by symbol and user email
    public Map<String, AttributeValue> getAlert(String stockSymbol, String email) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("symbol", AttributeValue.builder().s(stockSymbol).build());
            key.put("email", AttributeValue.builder().s(email).build());

            GetItemRequest request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build();

            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();
            if (returnedItem != null) {
                return returnedItem;
            } else {
                System.out.println("No alert found.");
                return null;
            }
        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.err.println("Unable to get the alert. Error: " + e.getMessage());
            return null;
        }
    }

    // Gets all the alerts base off the email
    public List<Map<String, Object>> getAllAlertsByEmail(String email) {
        try {
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":email", AttributeValue.builder().s(email).build());

            QueryRequest queryRequest = QueryRequest.builder()
                    .tableName(tableName)
                    .indexName(indexName) // Use your Global Secondary Index (GSI)
                    .keyConditionExpression("email = :email")
                    .expressionAttributeValues(expressionValues)
                    .build();

            QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

            List<Map<String, AttributeValue>> items = queryResponse.items();
            return items.stream()
                    .map(this::convertAttributeValueMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private Map<String, Object> convertAttributeValueMap(Map<String, AttributeValue> attributeValueMap) {
        return attributeValueMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            AttributeValue value = entry.getValue();
                            if (value.s() != null) {
                                return value.s();
                            } else if (value.n() != null) {
                                return value.n();
                            } else if (value.bool() != null) {
                                return value.bool();
                            } else {
                                return value.toString(); // Default to String representation
                            }
                        }
                ));
    }

    // Delete a stock price alert from DynamoDB
    public void deleteAlert(String stockSymbol, String email) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("symbol", AttributeValue.builder().s(stockSymbol).build());
            key.put("email", AttributeValue.builder().s(email).build());

            DeleteItemRequest request = DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build();

            dynamoDbClient.deleteItem(request);
            System.out.println("Successfully deleted the alert.");
        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.err.println("Unable to delete the alert. Error: " + e.getMessage());
        }
    }
}
