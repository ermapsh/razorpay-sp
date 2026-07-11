package com.ermapsh.razorpay.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Void>> health() {
        return ApiResponse.ok("Live", null);
    }
}
