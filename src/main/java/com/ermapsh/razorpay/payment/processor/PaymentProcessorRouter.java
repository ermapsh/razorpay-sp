package com.ermapsh.razorpay.payment.processor;

import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessor;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request){
        PaymentProcessor processor = paymentProcessor.get(request.paymentMethod());
        if(processor == null){
            throw new IllegalArgumentException("No Payment Processor registered for this method");
        }
        return processor.charge(request);
    }
}
