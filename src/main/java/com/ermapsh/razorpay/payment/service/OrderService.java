package com.ermapsh.razorpay.payment.service;

import com.ermapsh.razorpay.payment.dto.request.CreateOrderRequest;
import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public interface OrderService {

    Object createOrder(UUID merchantId, CreateOrderRequest receipt);
}
