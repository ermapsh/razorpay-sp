package com.ermapsh.razorpay.valut.service.impl;

import com.ermapsh.razorpay.common.enums.CardBrand;
import com.ermapsh.razorpay.valut.config.VaultEncryptionConfig;
import com.ermapsh.razorpay.valut.dto.request.TokenizeRequest;
import com.ermapsh.razorpay.valut.dto.response.TokenizeResponse;
import com.ermapsh.razorpay.valut.entity.VaultCard;
import com.ermapsh.razorpay.valut.repository.CardTokenRepository;
import com.ermapsh.razorpay.valut.service.VaultService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.LuhnCheck;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;

    @Override
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String lastFour = request.pan().substring(request.pan().length()-4);
        String bin = request.pan().substring(0,6);
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
                cardHolderName("").
                lastFour(lastFour).
                expiryMonth(request.expiryMonth().toString()).
                expiryYear(request.expiryYear().toString()).
                build();

        return null;
    }

    private CardBrand detectBrand( String pan) {
        if(pan.startsWith("4")) return CardBrand.VISA;
        if(pan.startsWith("5") || pan.startsWith("2")) return  CardBrand.MASTERCARD;
        if(pan.startsWith("37") || pan.startsWith("34")) return  CardBrand.AMEX;
        return CardBrand.RUPAY;
    }
}
