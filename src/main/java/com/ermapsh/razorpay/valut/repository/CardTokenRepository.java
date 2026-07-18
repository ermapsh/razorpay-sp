package com.ermapsh.razorpay.valut.repository;

import com.ermapsh.razorpay.valut.entity.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
}