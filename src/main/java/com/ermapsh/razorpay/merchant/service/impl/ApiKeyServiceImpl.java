package com.ermapsh.razorpay.merchant.service.impl;

import com.ermapsh.razorpay.common.enums.ApiResponse;
import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.merchant.dto.request.CreateApiRequest;
import com.ermapsh.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ermapsh.razorpay.merchant.entity.ApiKey;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import com.ermapsh.razorpay.merchant.repository.ApiKeyRepository;
import com.ermapsh.razorpay.merchant.repository.MerchantRepository;
import com.ermapsh.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;

    public ApiKeyCreateResponse create(UUID merchantId, @Valid CreateApiRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId).
                orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + merchantId));

        String keyId = "rzp" + request.environment().name().toLowerCase()+"_"+ UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        
        String rawSecret = "big_random_secret";

        ApiKey apiKey = ApiKey.builder().
                merchant(merchant).
                keyId(keyId).
                keySecretHash(rawSecret).
                env(request.environment()).
                build();

        ApiKey savedApiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(savedApiKey.getId(), keyId, rawSecret, request.environment());
    }
}
