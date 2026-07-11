package com.ermapsh.razorpay.payment.service;

import com.ermapsh.razorpay.payment.dto.request.PaymentInitRequest;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


public interface PaymentService {
    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);
    PaymentResponse capture(UUID merchantId, UUID paymentId);
}
