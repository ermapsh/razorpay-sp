package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefundRecordRepository extends JpaRepository<Refund, UUID> {
}