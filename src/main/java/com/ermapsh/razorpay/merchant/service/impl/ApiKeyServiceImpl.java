package com.ermapsh.razorpay.merchant.service.impl;

import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.common.util.RandomizerUtil;
import com.ermapsh.razorpay.merchant.dto.request.CreateApiRequest;
import com.ermapsh.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ermapsh.razorpay.merchant.dto.response.GetAllApiByMerchant;
import com.ermapsh.razorpay.merchant.entity.ApiKey;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import com.ermapsh.razorpay.merchant.mapper.ApiKeyCreateResponseMapper;
import com.ermapsh.razorpay.merchant.repository.ApiKeyRepository;
import com.ermapsh.razorpay.merchant.repository.MerchantRepository;
import com.ermapsh.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;
    private final ApiKeyCreateResponseMapper apiKeyCreateResponseMapper;
    private static final Logger log = LoggerFactory.getLogger(ApiKeyServiceImpl.class);

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, @Valid CreateApiRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId).
                orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + merchantId));

        String keyId = "rzp" + request.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64();
        
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder().
                merchant(merchant).
                keyId(keyId).
                keySecretHash(rawSecret).
                environment(request.environment()).
                build();

        ApiKey savedApiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(savedApiKey.getId(), keyId, rawSecret, request.environment());
    }


    @Override
    public List<GetAllApiByMerchant> listByMerchant(UUID merchantId) {

        merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant not found with id: " + merchantId));

//        List<ApiKey> apiKeys = apiKeyRepository.findByMerchant_Id(merchantId);
        List<ApiKey> apiKeys = apiKeyRepository.findByMerchant_IdAndEnabledTrue(merchantId);

        return apiKeyCreateResponseMapper.toResponseList(apiKeys);
//            return apiKeys.stream().map(apiKey -> new GetAllApiByMerchant(
//                        apiKey.getId(),
//                        apiKey.getKeyId(),
//                        apiKey.getEnvironment(),
//                        apiKey.isEnabled(),
//                        apiKey.getLastUsedAt(),
//                        apiKey.getCreatedAt()
//                ))
//                .toList();
    }


    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId).filter(k-> k.getMerchant().getId().equals(merchantId)).orElseThrow(
                ()-> new ResourceNotFoundException("ApiKey: "+ keyId));
        apiKey.setEnabled(false);
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotateKey(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId).filter(k-> k.getMerchant().getId().equals(merchantId)).orElseThrow(
                ()-> new ResourceNotFoundException("ApiKey: "+ keyId));

        if(!apiKey.isEnabled()){
            throw new RuntimeException("Cannot rotate disabled API KEY: " + keyId);
        }

        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(newRawSecret); //TODO
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusDays(1));
        apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(
                apiKey.getId(),
                apiKey.getKeyId(),
                apiKey.getKeySecretHash(),
                apiKey.getEnvironment()
        );
    }
}
