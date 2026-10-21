package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.payment.entity.Order;
import com.ermapsh.razorpay.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder(Order order);

    List<Payment> findByPaymentStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime globalWindow);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.id= :paymentId and p.merchantId= :merchantId")
    Optional<Payment> findByIdAndMerchantIdForUpdate(UUID merchantId, UUID paymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.id= :paymentId")
    Optional<Payment> findByIdForUpdate(UUID paymentId);
}