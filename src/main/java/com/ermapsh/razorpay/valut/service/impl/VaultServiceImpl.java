package com.ermapsh.razorpay.valut.service.impl;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.CardBrand;
import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.common.util.RandomizerUtil;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;
import com.ermapsh.razorpay.payment.processor.PaymentProcessorRouter;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.ermapsh.razorpay.valut.config.VaultEncryptionConfig;
import com.ermapsh.razorpay.valut.dto.request.TokenizeRequest;
import com.ermapsh.razorpay.valut.dto.response.TokenizeResponse;
import com.ermapsh.razorpay.valut.entity.CardToken;
import com.ermapsh.razorpay.valut.entity.VaultCard;
import com.ermapsh.razorpay.valut.repository.CardTokenRepository;
import com.ermapsh.razorpay.valut.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;


    @Override
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String lastFour = request.pan().substring(request.pan().length() - 4);
        String bin = request.pan().substring(0, 6);
        CardBrand cardBrand = detectBrand(request.pan());

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek)
                .encrypt(request.pan().getBytes(StandardCharsets.UTF_8));

        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCard vaultCard = VaultCard.builder().
                encryptedDek(encryptedDek).
                encryptedPan(encryptedPan).
                bin(bin).
                brand(cardBrand).
                cardHolderName(request.cardHolderName()).
                lastFour(lastFour).
                expiryMonth(request.expiryMonth().toString()).
                expiryYear(request.expiryYear().toString()).
                build();

        String token = "tok" + RandomizerUtil.randomBase64(32);

        CardToken cardToken = CardToken.builder()
                .vaultCard(vaultCard)
                .token(token)
                .customer(request.customerId())
                .merchant(merchantId)
                .build();

        cardTokenRepository.save(cardToken);

        return new TokenizeResponse(token, lastFour, cardBrand, request.expiryMonth(), request.expiryYear());
    }

    @Override
    public PaymentProcessorResponse charge(String token, UUID paymentId, Money amount, Map<String, Object> methodDetails) {
        byte[] panBytes = null;
        try {
            CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token).orElseThrow(() ->
                    new ResourceNotFoundException("Card Token not found : " + token));

            VaultCard vaultCard = cardToken.getVaultCard();


            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(paymentId, pan, expiry, amount, methodDetails);
            PaymentProcessorResponse res = paymentProcessorRouter.charge(paymentProcessorRequest);

            log.info("Vault charge registered, token={}**** ", token.substring(0, 4));
            return res;
        } catch (Exception e) {
            log.info("Vault charge failed, token={}**** ", token.substring(0, 4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally {
            if (panBytes != null) Arrays.fill(panBytes, (byte) 0);
        }
    }

    private CardBrand detectBrand(String pan) {
        if (pan.startsWith("4")) return CardBrand.VISA;
        if (pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        if (pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;
        return CardBrand.RUPAY;
    }
}
