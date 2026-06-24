package com.ermapsh.razorpay.merchant.dto.response;

import com.ermapsh.razorpay.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetAllApiByMerchant(
    UUID id,
    String keyId,
    Environment environment,
    boolean enabled,
    LocalDateTime lastUsedAt,
    LocalDateTime createdAt
) {
}
