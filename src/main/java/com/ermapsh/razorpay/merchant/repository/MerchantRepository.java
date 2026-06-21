package com.ermapsh.razorpay.merchant.repository;

import com.ermapsh.razorpay.merchant.entity.Merchant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    boolean existsByEmail(@Email(message = "Email should be valid") @NotNull(message="Email is required") String email);
}