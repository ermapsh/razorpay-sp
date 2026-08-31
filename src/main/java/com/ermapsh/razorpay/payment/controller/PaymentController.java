package com.ermapsh.razorpay.payment.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import com.ermapsh.razorpay.merchant.security.MerchantContext;
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
    private final MerchantContext merchantContext;

    @PostMapping("")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiate(@Valid @RequestBody PaymentInitRequest request) {
        UUID merchantId = merchantContext.getMerchantId();
        return ApiResponse.created(
                "Payment initiated",
                paymentService.initiate(merchantId, request)
        );
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<ApiResponse<PaymentResponse>> capture(@RequestParam UUID paymentId) {
        UUID merchantId = merchantContext.getMerchantId();
        return ApiResponse.ok(
                "Payment capture",
                paymentService.capture(merchantId, paymentId)
        );
    }

}
