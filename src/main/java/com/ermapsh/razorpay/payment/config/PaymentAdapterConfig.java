package com.ermapsh.razorpay.payment.config;

import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.payment.gateway.PaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.adapter.WalletPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;
    private final WalletPaymentAdapter walletPaymentAdapter;

    @Bean
    Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD,cardPaymentAdapter,
                PaymentMethod.UPI, upiPaymentAdapter,
                PaymentMethod.NET_BANKING, netBankingAdapter,
                PaymentMethod.WALLET, walletPaymentAdapter
        );
    }

}
