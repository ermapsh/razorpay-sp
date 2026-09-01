package com.ermapsh.razorpay.merchant.security;

import com.ermapsh.razorpay.merchant.entity.ApiKey;
import com.ermapsh.razorpay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String BASIC_PREFIX = "Basic ";

    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder BCrypt = new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("Incoming request: {}", request.getRequestURI());

        try {

            String authorizationHeader =
                    request.getHeader("Authorization");

            // Same idea as JwtAuthenticationFilter:
            // If this request doesn't contain Basic Auth,
            // let the request continue.
            if (authorizationHeader == null ||
                    !authorizationHeader.startsWith(BASIC_PREFIX)) {

                filterChain.doFilter(request, response);
                return;
            }

            // Decode keyId:secret
            String[] credentials = decodeHeader(authorizationHeader);

            if (credentials == null) {
                throw new BadCredentialsException(
                        "Malformed API key header"
                );
            }

            String keyId = credentials[0];
            String secretKey = credentials[1];

            // Find API key
            ApiKey apiKey = apiKeyRepository
                    .findByKeyId(keyId)
                    .orElseThrow(() ->
                            new BadCredentialsException(
                                    "Invalid API key"
                            )
                    );

            // Check enabled
            if (!apiKey.isEnabled()) {
                throw new BadCredentialsException(
                        "API key is disabled"
                );
            }

            // Check secret
            if (!secretMatches(secretKey, apiKey)) {
                throw new BadCredentialsException(
                        "Invalid API key"
                );
            }

            // Create authentication
            var auth =
                    new UsernamePasswordAuthenticationToken(
                            keyId,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "API_KEY_ROLE"
                                    )
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);

            // Set MerchantContext
            merchantContext.setMerchantId(
                    apiKey.getMerchant().getId()
            );

            merchantContext.setKeyId(
                    apiKey.getKeyId()
            );

            log.info(
                    "API key authenticated successfully. keyId={}, merchantId={}",
                    apiKey.getKeyId(),
                    apiKey.getMerchant().getId()
            );

            // Continue
            filterChain.doFilter(request, response);

        } catch (Exception e) {

            log.error(
                    "API key authentication failed: {}",
                    e.getMessage()
            );

            SecurityContextHolder.clearContext();
            merchantContext.clear();

            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    e
            );
        }
    }

    private boolean secretMatches(
            String rawSecret,
            ApiKey apiKey
    ) {

        if (BCrypt.matches(
                rawSecret,
                apiKey.getKeySecretHash()
        )) {
            return true;
        }

        boolean gracePeriod =
                apiKey.getGracePeriodExpiresAt() != null
                        && LocalDateTime.now().isBefore(
                        apiKey.getGracePeriodExpiresAt()
                );

        return gracePeriod
                && apiKey.getPreviousKeySecretHash() != null
                && BCrypt.matches(
                rawSecret,
                apiKey.getPreviousKeySecretHash()
        );
    }

    private String[] decodeHeader(String header) {

        try {

            String encoded =
                    header.substring(BASIC_PREFIX.length());

            String decoded =
                    new String(
                            Base64.getDecoder().decode(encoded),
                            StandardCharsets.UTF_8
                    );

            int colon = decoded.indexOf(":");

            if (colon <= 0) {
                return null;
            }

            String keyId =
                    decoded.substring(0, colon);

            String secretKey =
                    decoded.substring(colon + 1);

            if (secretKey.isBlank()) {
                return null;
            }

            return new String[]{
                    keyId,
                    secretKey
            };

        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}