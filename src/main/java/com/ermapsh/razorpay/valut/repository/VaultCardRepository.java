package com.ermapsh.razorpay.valut.repository;

import com.ermapsh.razorpay.valut.entity.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaultCardRepository extends JpaRepository<VaultCard, UUID> {
}