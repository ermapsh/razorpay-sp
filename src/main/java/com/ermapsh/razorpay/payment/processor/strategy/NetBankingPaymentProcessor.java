package com.ermapsh.razorpay.payment.processor.strategy;

import com.ermapsh.razorpay.payment.processor.PaymentProcessor;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
// processor call here network banking util
public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        return null;
    }

}
