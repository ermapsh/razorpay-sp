package com.ermapsh.razorpay.payment.dto.response;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID merchantId,
        String idempotency,
        String bankReference,
        Money money,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails,
        PaymentStatus paymentStatus,
        String errorCode,
        String errorDescription,
        LocalDateTime authorizedAt,
        LocalDateTime capturedAt,
        LocalDateTime failedAt,
        LocalDateTime refundedAt
) {
}