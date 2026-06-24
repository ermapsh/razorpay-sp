package com.ermapsh.razorpay.merchant.repository;

import com.ermapsh.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_Id(UUID merchantId);
    List<ApiKey> findByMerchant_IdAndEnabledTrue(UUID merchantId);
}