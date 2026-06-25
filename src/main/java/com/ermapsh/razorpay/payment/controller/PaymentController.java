package com.ermapsh.razorpay.payment.controller;

import com.ermapsh.razorpay.common.enums.ApiResponse;
import com.ermapsh.razorpay.payment.dto.request.PaymentInitRequest;
import com.ermapsh.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Value("${merchant.id}")
    private UUID merchantId;

    @PostMapping("")
    public ResponseEntity<?> initiate(@Valid @RequestBody PaymentInitRequest request){
//            return ResponseEntity.accepted().body("hii");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(
                        HttpStatus.CREATED.value(),
                        "Payment initiated",
                        paymentService.initiate(merchantId, request),
                        null
                )
        );
    }

}
