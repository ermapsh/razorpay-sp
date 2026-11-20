package com.ermapsh.razorpay.operations.repository;

import com.ermapsh.razorpay.operations.entity.DLQEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DLQEventRepository extends JpaRepository<DLQEvent, UUID> {
}