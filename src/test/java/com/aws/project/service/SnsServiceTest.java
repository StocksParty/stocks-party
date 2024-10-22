package com.aws.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SnsServiceTest {

    @Mock
    private SnsClient snsClient;

    @InjectMocks
    private SnsService snsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void testSendNotification() {
//        snsService.sendPriceAlert("Test message");
//
//        // Verify that the SNS client was called with the correct request
//        verify(snsClient, times(1)).publish(any(PublishRequest.class));
//    }
}
