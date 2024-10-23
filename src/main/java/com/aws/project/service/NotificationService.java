package com.aws.project.service;

import com.aws.project.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

@Service
@RequiredArgsConstructor
@Import(AppConfig.class)
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final SnsClient snsClient;
    private final SesClient sesClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    @Value("${aws.ses.sender-email}")
    private String senderEmail;

    public void sendPriceAlertByEmail(String email, String stock, double price) {
        String message = "The stock price of " + stock + " has reached $" + price
                + ". This is your target price alert.";
        String subject = "Stock Price Alert: " + stock;

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(email).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder()
                                .text(Content.builder().data(message).build())
                                .build())
                        .build())
                .source(senderEmail)
                .build();

        sesClient.sendEmail(emailRequest);
    }

    // Subscribe a user's email to the SNS topic
    public void subscribeEmailToTopic(String email) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("email")
                .endpoint(email)
                .returnSubscriptionArn(true)
                .topicArn(topicArn)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);
        logger.info("Subscription ARN: " + response.subscriptionArn());
    }

    // Send email notification to SNS topic
    public void sendPriceAlertBySNS(String email, String phone, String stockSymbol, double currentPrice) {
        subscribeEmailToTopic(email);

        String message = "The stock price of " + stockSymbol + " has reached $" + currentPrice
                + ". This is your target price alert.";
        String subject = "Stock Price Alert: " + stockSymbol;

        try {
            // Build the PublishRequest
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .subject(subject)
                    .phoneNumber(phone)
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

    public void sendTestNotification(String email, String phone, String stockSymbol, double currentPrice) {
        subscribeEmailToTopic(email);

        String message = "This is a test notification for  " + stockSymbol
                + ". This is your target price alert.";
        String subject = "TEST Stock Price Alert: " + stockSymbol;

        try {
            // Build the PublishRequest
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .subject(subject)
                    .phoneNumber(phone)
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