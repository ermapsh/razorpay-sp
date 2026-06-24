package com.ermapsh.razorpay.payment.service;

import com.ermapsh.razorpay.payment.dto.request.CreateOrderRequest;
import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    CreateOrderResponse createOrder(UUID merchantId, CreateOrderRequest receipt);

    CreateOrderResponse getOrderById(UUID merchantId, UUID orderId);

    CreateOrderResponse cancel(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}
