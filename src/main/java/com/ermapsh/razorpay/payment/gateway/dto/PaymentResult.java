package com.ermapsh.razorpay.payment.gateway.dto;

public sealed interface PaymentResult permits PaymentResult.Pending ,PaymentResult.Failure, PaymentResult.Success {
    record Pending(String registrationRef)implements PaymentResult{}
//    record Success(String registrationRef)implements PaymentResult{}
    record Failure(String errorCode, String errorDescription) extends Throwable implements PaymentResult{}

    record Success(String bankReference)implements PaymentResult{}
}
