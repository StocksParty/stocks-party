package com.aws.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * Class for creating all the necessarily beans
 */
public class AppConfig {

    /**
     * RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /**
     * DynamoDB bean
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * SNS Client bean
     */
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}
