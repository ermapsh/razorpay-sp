package com.ermapsh.razorpay.merchant.service;

import com.ermapsh.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ermapsh.razorpay.merchant.dto.response.MerchantSignupResponse;

public interface AuthService {
    MerchantSignupResponse signup(MerchantSignupRequest request);
}
