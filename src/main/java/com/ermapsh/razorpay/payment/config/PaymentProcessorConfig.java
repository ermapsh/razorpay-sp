package com.ermapsh.razorpay.payment.config;

import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.payment.processor.PaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.NetbankingPaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import com.ermapsh.razorpay.payment.processor.strategy.WalletProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
            PaymentMethod.CARD, new CardPaymentProcessor(),
            PaymentMethod.UPI, new UpiPaymentProcessor(),
            PaymentMethod.NET_BANKING, new NetbankingPaymentProcessor(),
            PaymentMethod.WALLET, new WalletProcessor()
        );
    }
}
