package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.entity.Order;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    CreateOrderResponse getById(UUID id, UUID merchantId);
    boolean existsByMerchantIdAndReceipt(UUID merchantId, @Size(max = 100) String receipt);
    Optional<Order> findByIdAndMerchantId(UUID id, UUID merchantId);
}