package com.ermapsh.razorpay.payment.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import com.ermapsh.razorpay.payment.dto.request.PaymentInitRequest;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import com.ermapsh.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Value("${merchant.id}")
    private UUID merchantId; // will come through auth middleware

    @PostMapping("")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiate(@Valid @RequestBody PaymentInitRequest request) {
//            return ResponseEntity.accepted().body("hii");
        return ApiResponse.created(
                "Payment initiated",
                paymentService.initiate(merchantId, request)
        );
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<ApiResponse<PaymentResponse>> capture(@RequestParam UUID paymentId) {
        return ApiResponse.ok(
                "Payment capture",
                paymentService.capture(merchantId, paymentId)
        );
    }

}
