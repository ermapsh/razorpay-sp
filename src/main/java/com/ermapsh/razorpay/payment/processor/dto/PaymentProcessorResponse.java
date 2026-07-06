package com.ermapsh.razorpay.payment.processor.dto;

public sealed interface PaymentProcessorResponse permits
        PaymentProcessorResponse.Pending,
        PaymentProcessorResponse.Success,
        PaymentProcessorResponse.Failure {

    record Pending(String processorRef) implements PaymentProcessorResponse{}

    record Success(String bankReference)implements PaymentProcessorResponse{}

    record Failure(String errorCode, String errorDescription)implements PaymentProcessorResponse{}
}