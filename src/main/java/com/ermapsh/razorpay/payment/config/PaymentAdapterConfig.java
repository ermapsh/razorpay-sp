package com.ermapsh.razorpay.payment.config;

import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.payment.gateway.PaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.WalletPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PaymentAdapterConfig {

    @Bean
    Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.UPI, new UpiPaymentAdapter(),
                PaymentMethod.NET_BANKING, new NetBankingAdapter(),
                PaymentMethod.WALLET, new WalletPaymentAdapter()
        );
    }

}
