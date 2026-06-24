package com.ermapsh.razorpay.payment.service.impl;

import com.ermapsh.razorpay.common.exception.DuplicateResourceException;
import com.ermapsh.razorpay.payment.dto.request.CreateOrderRequest;
import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.entity.Order;
import com.ermapsh.razorpay.payment.repository.OrderRepository;
import com.ermapsh.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-expiry-minutes}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(UUID merchantId, CreateOrderRequest request) {
        System.out.println("${defaultOrderExpiryMinutes}");
        if (request.receipt() != null &&
                orderRepository.existsByMerchantAndReceipt(merchantId, request.receipt())) {
            throw new DuplicateResourceException("Order with receipt already exists" + request.receipt());
        }

        System.out.println(defaultOrderExpiryMinutes);

        Order newOrder = Order.builder().
                receipt(request.receipt()).
                amount(request.amount()).
                notes(request.notes()).
                expiresAt(request.expiresAt() != null ?  request.expiresAt(): LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes)).
                merchant(merchantId).
                build();

        Order savedOrder = orderRepository.save(newOrder);

        // TODO: publish kafka event about order creation

        return new CreateOrderResponse(
                savedOrder.getId(),
                savedOrder.getMerchant(),
                savedOrder.getReceipt(),
                savedOrder.getAmount(),
                savedOrder.getStatus(),
                savedOrder.getAttempts(),
                savedOrder.getNotes(),
                savedOrder.getExpiresAt(),
                null
        );
    }

}
