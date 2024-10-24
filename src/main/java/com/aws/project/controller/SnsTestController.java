package com.aws.project.controller;

import com.aws.project.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sns")
public class SnsTestController {

    private final NotificationService notificationService;

    public SnsTestController(NotificationService snsService) {
        this.notificationService = snsService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testSns() {
        notificationService.sendPriceAlertBySNS("test@example.com","99999999", "AAPL", 150.00);
        return ResponseEntity.ok("SNS Notification Sent");
    }
}
