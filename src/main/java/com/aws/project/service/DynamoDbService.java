package com.aws.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DynamoDbService {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDbService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }
    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    // Save a new stock price alert to DynamoDB
    public void saveAlert(String stockSymbol, double targetPrice, String userEmail) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("symbol", AttributeValue.builder().s(stockSymbol).build());
            item.put("userEmail", AttributeValue.builder().s(userEmail).build());
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
    public Map<String, AttributeValue> getAlert(String stockSymbol, String userEmail) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("symbol", AttributeValue.builder().s(stockSymbol).build());
            key.put("userEmail", AttributeValue.builder().s(userEmail).build());

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

    // Delete a stock price alert from DynamoDB
    public void deleteAlert(String stockSymbol, String userEmail) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("symbol", AttributeValue.builder().s(stockSymbol).build());
            key.put("userEmail", AttributeValue.builder().s(userEmail).build());

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
