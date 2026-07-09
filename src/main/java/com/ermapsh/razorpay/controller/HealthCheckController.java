package com.ermapsh.razorpay.controller;

import com.ermapsh.razorpay.common.enums.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> health() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(
                        HttpStatus.OK.value(),
                        "Live",
                        null,
                        null
                )
        );
    }
}
