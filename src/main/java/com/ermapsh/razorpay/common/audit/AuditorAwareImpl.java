package com.ermapsh.razorpay.common.audit;


import com.ermapsh.razorpay.merchant.security.MerchantContext;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MerchantContext merchantContext;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String keyId = merchantContext.getKeyId();
            UUID merchantId = merchantContext.getMerchantId();

            if (keyId != null && !keyId.isBlank()) return Optional.of(keyId);

            if (merchantId != null)
                return Optional.of("merchant_id: " + merchantId);
        } catch (Exception ignored) {

        }

        return Optional.of("SYSTEM");
    }
}
