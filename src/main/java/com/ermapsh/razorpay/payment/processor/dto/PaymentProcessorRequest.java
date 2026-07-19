package com.ermapsh.razorpay.payment.processor.dto;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(

        UUID processingId,

        @NotNull
        UUID paymentId,

        @NotNull
        PaymentMethod paymentMethod,

        @NotNull
        Money money,

        String pan,

        String expiry,

        @NotNull
        Map<String, Object> methodDetails
) {

    public static PaymentProcessorRequest card(UUID paymentId, String pan, String expiry, Money money, Map<String, Object> methodDetails) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, PaymentMethod.CARD, money, pan, expiry, methodDetails);
    }


    public static PaymentProcessorRequest nonCard(UUID paymentId, PaymentMethod paymentMethod, Money money, Map<String, Object> methodDetails) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, paymentMethod, money, null, null, methodDetails);
    }

}
