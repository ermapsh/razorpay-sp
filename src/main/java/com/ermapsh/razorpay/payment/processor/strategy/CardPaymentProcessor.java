package com.ermapsh.razorpay.payment.processor.strategy;

import com.ermapsh.razorpay.payment.processor.PaymentProcessor;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;

// payment processor call here different type of cards here visa, mastercard and many more
public class CardPaymentProcessor implements PaymentProcessor
{
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
