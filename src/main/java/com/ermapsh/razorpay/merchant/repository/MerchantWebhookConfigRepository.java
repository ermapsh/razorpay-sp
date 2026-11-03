package com.ermapsh.razorpay.merchant.repository;

import com.ermapsh.razorpay.merchant.entity.MerchantWebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantWebhookConfigRepository extends JpaRepository<MerchantWebhookConfig, UUID> {
    List<MerchantWebhookConfig> findAllByMerchant_Id(UUID merchantId);
    Optional<MerchantWebhookConfig> findByIdAndMerchant_Id(UUID id,UUID merchantId);

    List<MerchantWebhookConfig> findByMerchant_IdAndEnabledTrue(UUID merchantId);
}