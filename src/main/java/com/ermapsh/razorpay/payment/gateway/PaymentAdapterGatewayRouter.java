package com.ermapsh.razorpay.payment.gateway;

import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentAdapterGatewayRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapters;

    public PaymentResult initiate(PaymentRequest request){

        log.info("request method is {}", request.method());
        log.info("request methodDetails is {}", request.methodDetails());

        PaymentAdapter adapter = paymentAdapters.get(request.method());
        log.info("request adapter is {}", adapter);

        if(adapter == null){
            throw new IllegalArgumentException("No payment adapter registered for method : "+ request.method());
        }
        return adapter.initiate(request);
    }

    public PaymentResult capture(PaymentMethod paymentMethod, UUID paymentId) {
        PaymentAdapter paymentAdapter = paymentAdapters.get(paymentMethod);

        if(paymentAdapter == null){
            throw new IllegalArgumentException("No Payment adapter registered for this method");
        }
        return paymentAdapter.capture(paymentId);
    }
}
