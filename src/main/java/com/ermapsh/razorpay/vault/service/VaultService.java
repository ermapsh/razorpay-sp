package com.ermapsh.razorpay.vault.service;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.ermapsh.razorpay.vault.dto.request.TokenizeRequest;
import com.ermapsh.razorpay.vault.dto.response.TokenizeResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    public TokenizeResponse tokenize(@Valid TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(String token, UUID paymentId, Money amount, Map<String, Object> methodDetails);
}
