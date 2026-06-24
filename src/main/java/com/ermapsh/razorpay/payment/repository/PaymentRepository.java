package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.payment.entity.Order;
import com.ermapsh.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder(Order order);
}