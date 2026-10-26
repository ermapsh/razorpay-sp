package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.common.enums.OutboxStatus;
import com.ermapsh.razorpay.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByOutboxStatusOrderByCreatedAtAsc(OutboxStatus outboxStatus);
}