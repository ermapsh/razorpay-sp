package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.payment.entity.Order;
import com.ermapsh.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder(Order order);

    List<Payment> findByPaymentStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime globalWindow);
}