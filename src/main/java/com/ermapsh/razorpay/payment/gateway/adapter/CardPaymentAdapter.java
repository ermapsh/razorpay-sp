package com.ermapsh.razorpay.payment.gateway.adapter;

import com.ermapsh.razorpay.payment.gateway.PaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.ermapsh.razorpay.valut.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        String token = request.methodDetails().get("token").toString();

        PaymentProcessorResponse res = vaultService.charge(token, request.paymentId(), request.amount(), request.methodDetails());

        return switch (res){
            case PaymentProcessorResponse.Pending pending ->  new PaymentResult.Pending(pending.processorRef());
            case PaymentProcessorResponse.Success success ->  new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure ->  new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());

        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
