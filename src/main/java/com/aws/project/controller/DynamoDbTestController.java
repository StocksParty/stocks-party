package com.aws.project.controller;

import com.aws.project.service.DynamoDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dynamodb")
@RequiredArgsConstructor
public class DynamoDbTestController {

    private final DynamoDbService dynamoDbService;

    // Test endpoint to save a test alert
    @PostMapping("/save")
    public ResponseEntity<String> saveTestAlert(
            @RequestParam String symbol,
            @RequestParam String email,
            @RequestParam double targetPrice) {

        dynamoDbService.saveAlert(symbol, targetPrice, email);
        return ResponseEntity.ok("Test alert saved to DynamoDB!");
    }

    // Test endpoint to retrieve an alert
    @GetMapping("/alert")
    public ResponseEntity<Map<String, Object>> getAlert(
            @RequestParam String symbol,
            @RequestParam String email) {

        Map<String, AttributeValue> alert = dynamoDbService.getAlert(symbol, email);
        if (alert != null) {
            // Convert AttributeValue map to a regular Object map
            Map<String, Object> convertedAlert = convertAttributeValueMap(alert);
            return ResponseEntity.ok(convertedAlert);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper method to convert Map<String, AttributeValue> to Map<String, Object>
    private Map<String, Object> convertAttributeValueMap(Map<String, AttributeValue> attributeValueMap) {
        Map<String, Object> resultMap = new HashMap<>();

        for (Map.Entry<String, AttributeValue> entry : attributeValueMap.entrySet()) {
            AttributeValue attributeValue = entry.getValue();
            // Convert AttributeValue to its corresponding object type
            if (attributeValue.s() != null) {
                resultMap.put(entry.getKey(), attributeValue.s());
            } else if (attributeValue.n() != null) {
                resultMap.put(entry.getKey(), Double.valueOf(attributeValue.n()));
            } else if (attributeValue.bool() != null) {
                resultMap.put(entry.getKey(), attributeValue.bool());
            }
        }

        return resultMap;
    }
}
