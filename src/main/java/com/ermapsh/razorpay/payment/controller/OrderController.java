package com.ermapsh.razorpay.payment.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import com.ermapsh.razorpay.merchant.security.MerchantContext;
import com.ermapsh.razorpay.payment.dto.request.CreateOrderRequest;
import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import com.ermapsh.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;

    @PostMapping("")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return ApiResponse.created("order created successfully", orderService.createOrder(merchantContext.getMerchantId(), request));
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> getOrderById(@PathVariable UUID orderId) {
        return ApiResponse.ok("order fetched successfully", orderService.getOrderById(merchantContext.getMerchantId(), orderId));
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> cancelOrderById(@PathVariable UUID orderId) {
        return ApiResponse.ok("order cancel successfully", orderService.cancel(merchantContext.getMerchantId(), orderId));
    }

    @PostMapping("/{orderId}/payment_list")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> listPayments(@PathVariable UUID orderId) {
        return ApiResponse.ok("order created successfully", orderService.listPayments(merchantContext.getMerchantId(), orderId));
    }

}
