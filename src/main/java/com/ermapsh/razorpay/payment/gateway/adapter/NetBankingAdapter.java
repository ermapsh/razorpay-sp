package com.ermapsh.razorpay.payment.gateway.adapter;

import com.ermapsh.razorpay.payment.gateway.PaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;
import com.ermapsh.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class NetBankingAdapter implements PaymentAdapter {

    private NetBankingPaymentProcessor netbankingPaymentProcessor;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        log.info("initiate Payment with NetBanking PaymentProcessor, paymentId: {}", request.paymentId());
        return null;
    }
}
