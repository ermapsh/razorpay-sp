package com.ermapsh.razorpay.payment.config;

import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.payment.processor.PaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.WalletProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final WalletProcessor walletProcessor;


    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
            PaymentMethod.CARD, cardPaymentProcessor,
            PaymentMethod.UPI, upiPaymentProcessor,
            PaymentMethod.NET_BANKING, netBankingPaymentProcessor,
            PaymentMethod.WALLET, walletProcessor
        );
    }
}
