package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.payment.entity.Order;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsByMerchantAndReceipt(UUID merchantId, @Size(max = 100) String receipt);
}