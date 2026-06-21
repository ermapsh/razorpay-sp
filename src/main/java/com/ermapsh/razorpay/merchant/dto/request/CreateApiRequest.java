package com.ermapsh.razorpay.merchant.dto.request;


import com.ermapsh.razorpay.common.enums.Environment;

public record CreateApiRequest(
        Environment environment
) {
}
