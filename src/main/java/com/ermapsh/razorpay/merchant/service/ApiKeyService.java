package com.ermapsh.razorpay.merchant.service;

import com.ermapsh.razorpay.merchant.dto.request.CreateApiRequest;
import com.ermapsh.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ermapsh.razorpay.merchant.dto.response.GetAllApiByMerchant;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiRequest request);
    List<GetAllApiByMerchant> listByMerchant(UUID merchantId);
    void revoke(UUID merchantId, UUID keyId);
    ApiKeyCreateResponse rotateKey(UUID merchantId, UUID keyId);
}
