package com.ermapsh.razorpay.merchant.dto.response;

import com.ermapsh.razorpay.common.enums.BusinessType;
import com.ermapsh.razorpay.common.enums.MerchantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

public record MerchantSignupResponse(
    UUID id,
    String name,
    BusinessType businessType,
    String businessName,
    String email,
    MerchantStatus merchantStatus
) {
}
