package com.aws.project.service;

import com.aws.project.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Service
@RequiredArgsConstructor
@Import(AppConfig.class)
public class SnsService {

    private static final Logger logger = LoggerFactory.getLogger(SnsService.class);
    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public void sendPriceAlert(String email, String stockSymbol, double currentPrice) {
        String message = "The stock price of " + stockSymbol + " has reached $" + currentPrice
                + ". This is your target price alert.";
        String subject = "Stock Price Alert: " + stockSymbol;

        try {
            // Build the PublishRequest
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .subject(subject)
                    .topicArn(topicArn)
                    .build();

            // Publish the message
            PublishResponse response = snsClient.publish(request);
            logger.info("Message sent to SNS with message ID: {}", response.messageId());

        } catch (SnsException e) {
            logger.error("Failed to send SNS notification", e);
            throw e;
        }
    }
}