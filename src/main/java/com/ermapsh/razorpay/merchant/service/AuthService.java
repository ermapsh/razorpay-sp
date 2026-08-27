package com.ermapsh.razorpay.merchant.service;

import com.ermapsh.razorpay.merchant.dto.request.AppUserLogInRequest;
import com.ermapsh.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ermapsh.razorpay.merchant.dto.response.AppUserLogInResponse;
import com.ermapsh.razorpay.merchant.dto.response.MerchantSignupResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantSignupResponse signup(MerchantSignupRequest request);
    AppUserLogInResponse login(AppUserLogInRequest request);
}
