package com.aws.project.controller;

import com.aws.project.service.SnsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sns")
public class SnsTestController {

    private final SnsService snsService;

    public SnsTestController(SnsService snsService) {
        this.snsService = snsService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testSns() {
        snsService.sendPriceAlert("test@example.com", "AAPL", 150.00);
        return ResponseEntity.ok("SNS Notification Sent");
    }
}
