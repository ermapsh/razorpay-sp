
package com.ermapsh.razorpay.merchant.service.impl;

import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.common.util.RandomizerUtil;
import com.ermapsh.razorpay.merchant.api.MerchantWebhookApi;
import com.ermapsh.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.ermapsh.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.ermapsh.razorpay.common.dto.WebhookTarget;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import com.ermapsh.razorpay.merchant.entity.MerchantWebhookConfig;
import com.ermapsh.razorpay.merchant.mapper.WebhookConfigMapper;
import com.ermapsh.razorpay.merchant.repository.MerchantRepository;
import com.ermapsh.razorpay.merchant.repository.MerchantWebhookConfigRepository;
import com.ermapsh.razorpay.merchant.service.WebhookConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WebhookConfigServiceImpl implements WebhookConfigService, MerchantWebhookApi {

    private final MerchantRepository merchantRepository;
    private final MerchantWebhookConfigRepository webhookConfigRepository;
    private final BytesEncryptor bytesEncryptor;
    private final WebhookConfigMapper webhookConfigMapper;

    @Override
    public WebhookConfigResponse create(
            UUID merchantId,
            UpdateWebhookConfigRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Merchant: " + merchantId));

        String rawSecret = RandomizerUtil.randomBase64(32);

        String encSecret = Base64.getEncoder().encodeToString(
                bytesEncryptor.encrypt(
                        rawSecret.getBytes(StandardCharsets.UTF_8)));

        MerchantWebhookConfig config = MerchantWebhookConfig.builder()
                .merchant(merchant)
                .targetUrl(request.targetUrl())
                .enabled(true)
                .eventTypes(request.eventTypes())
                .webhookSecret(encSecret)
                .build();

        config = webhookConfigRepository.save(config);

        // Return the raw secret only when creating the configuration.
        return webhookConfigMapper.toResponse(config, rawSecret);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookConfigResponse> list(UUID merchantId) {

        if (!merchantRepository.existsById(merchantId)) {
            throw new ResourceNotFoundException("Merchant: " + merchantId);
        }

        return webhookConfigRepository.findAllByMerchant_Id(merchantId)
                .stream()
                .map(config -> webhookConfigMapper.toResponse(config, null))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WebhookConfigResponse getById(
            UUID merchantId,
            UUID configId) {

        MerchantWebhookConfig config = findConfig(merchantId, configId);

        return webhookConfigMapper.toResponse(config, null);
    }

    @Override
    public WebhookConfigResponse update(
            UUID merchantId,
            UUID configId,
            UpdateWebhookConfigRequest request) {

        MerchantWebhookConfig config = findConfig(merchantId, configId);

        config.setTargetUrl(request.targetUrl());
        config.setEventTypes(request.eventTypes());

        MerchantWebhookConfig updated = webhookConfigRepository.save(config);

        // Do not regenerate or expose the existing secret on ordinary updates.
        return webhookConfigMapper.toResponse(updated, null);
    }

    @Override
    public void delete(UUID merchantId, UUID configId) {

        MerchantWebhookConfig config = findConfig(merchantId, configId);

        webhookConfigRepository.delete(config);
    }

    private MerchantWebhookConfig findConfig(
            UUID merchantId,
            UUID configId) {

        return webhookConfigRepository
                .findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Webhook configuration: " + configId));
    }

    @Override
    public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType) {

        return webhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId).stream().
                filter(config -> config.isSubscribedTo(eventType)).
                map(config -> {
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(config.getWebhookSecret().getBytes(StandardCharsets.UTF_8));
                    return new WebhookTarget(config.getId(), config.getTargetUrl(),
                            new String(decryptedSecretBytes, StandardCharsets.UTF_8)
                    );
                }).
                toList();
    }
}
