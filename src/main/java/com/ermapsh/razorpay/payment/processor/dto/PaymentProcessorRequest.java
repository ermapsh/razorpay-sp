package com.ermapsh.razorpay.payment.processor.dto;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record PaymentProcessorRequest (

        @NotNull
        PaymentMethod paymentMethod,

        @NotNull
        Money money,

        String pan,

        String expiry,

        @NotNull
        Map<String, Object> methodDetails
){
}
