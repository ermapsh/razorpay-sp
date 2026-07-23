package com.ermapsh.razorpay.payment.service;

import com.ermapsh.razorpay.payment.dto.request.PaymentInitRequest;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import com.ermapsh.razorpay.payment.entity.Payment;

import java.util.UUID;


public interface PaymentService {
    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);
    PaymentResponse capture(UUID merchantId, UUID paymentId);
    void resolveAuthorization(UUID paymentId, Boolean approve, String bankRef, String simBankErrorCode, String simulatedBankDecline);
}
