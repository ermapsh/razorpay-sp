package com.ermapsh.razorpay.vault.dto.response;

import com.ermapsh.razorpay.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand cardBrand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
