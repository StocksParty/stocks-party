package com.aws.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;

@Service
public class SnsService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String snsTopicArn;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    // Send a notification via SNS (can be email or SMS)
    public void sendNotification(String message, String phoneNumber) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            snsClient.publish(request);
            System.out.println("Notification sent successfully to phone: " + phoneNumber);
        } catch (SnsException e) {
            e.printStackTrace();
            System.err.println("Unable to send SMS. Error: " + e.getMessage());
        }
    }

    // Send an email notification via SNS
    public void sendEmailNotification(String message) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(snsTopicArn)
                    .build();

            snsClient.publish(request);
            System.out.println("Email notification sent successfully.");
        } catch (SnsException e) {
            e.printStackTrace();
            System.err.println("Unable to send email. Error: " + e.getMessage());
        }
    }
}
