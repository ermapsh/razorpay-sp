package com.ermapsh.razorpay.payment.service;

import com.ermapsh.razorpay.payment.dto.request.PaymentInitRequest;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


public interface PaymentService {
    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);
}
