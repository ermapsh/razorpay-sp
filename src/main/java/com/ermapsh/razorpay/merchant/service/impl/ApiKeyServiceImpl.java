package com.ermapsh.razorpay.merchant.service.impl;

import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.common.util.RandomizerUtil;
import com.ermapsh.razorpay.merchant.dto.request.CreateApiRequest;
import com.ermapsh.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ermapsh.razorpay.merchant.dto.response.GetAllApiByMerchant;
import com.ermapsh.razorpay.merchant.entity.ApiKey;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import com.ermapsh.razorpay.merchant.repository.ApiKeyRepository;
import com.ermapsh.razorpay.merchant.repository.MerchantRepository;
import com.ermapsh.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ModelMapper modelMapper;
    private final MerchantRepository merchantRepository;

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
                env(request.environment()).
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

        return apiKeys.stream()
                .map(apiKey -> new GetAllApiByMerchant(
                        apiKey.getId(),
                        apiKey.getKeyId(),
                        apiKey.getEnv(),
                        apiKey.isEnabled(),
                        apiKey.getLastUsedAt(),
                        apiKey.getCreatedAt()
                ))
                .toList();
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
                apiKey.getEnv()
        );
    }
}
