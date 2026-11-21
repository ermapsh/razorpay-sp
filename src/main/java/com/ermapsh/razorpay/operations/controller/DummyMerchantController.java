package com.ermapsh.razorpay.operations.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class DummyMerchantController {

    @PostMapping("/success")
    public ResponseEntity<Void> handleWebhookSuccess(
            @RequestBody Map<String, Object> req) {

        return ResponseEntity.noContent().build();
    }
}