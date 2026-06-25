package com.ermapsh.razorpay.payment.gateway;

import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;

public interface PaymentAdapter {
     void initiate(PaymentRequest request);
}
