package com.ermapsh.razorpay.payment.gateway;

import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;

public interface PaymentAdapter {
     PaymentResult initiate(PaymentRequest request);
}
