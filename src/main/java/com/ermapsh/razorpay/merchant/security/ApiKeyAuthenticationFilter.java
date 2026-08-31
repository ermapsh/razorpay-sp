package com.ermapsh.razorpay.merchant.security;

import com.ermapsh.razorpay.merchant.entity.ApiKey;
import com.ermapsh.razorpay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String BASIC_prefix = "Basic ";
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

        try {
            log.info("Incoming request: {}", request.getRequestURI());

            String header = request.getHeader("Authorization");

            // 1. Authorization header missing or not Basic Auth
            if (header == null || !header.startsWith(BASIC_prefix)) {
                throw new BadCredentialsException("Missing or invalid API key");
            }

            // 2. Decode Basic Auth
            String[] cred = decodeHeader(header);

            if (cred == null) {
                throw new BadCredentialsException("Malformed API key header");
            }

            String keyId = cred[0];
            String secretKey = cred[1];

            // 3. Find API key
            ApiKey apiKey = apiKeyRepository
                    .findByKeyId(keyId)
                    .orElseThrow(() ->
                            new BadCredentialsException("Invalid API key")
                    );

            // 4. Validate API key
            if (!apiKey.isEnabled()) {
                throw new BadCredentialsException("API key is disabled");
            }

            // 5. Validate secret
            if (!secretMatches(secretKey, apiKey)) {
                throw new BadCredentialsException("Invalid API key");
            }

            // 6. Create Spring Security authentication
            var auth = new UsernamePasswordAuthenticationToken(
                    keyId,
                    null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);

            // 7. Set merchant context
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

            // 8. Continue request
            filterChain.doFilter(request, response);

        } catch (BadCredentialsException e) {

            // Authentication failure → 401
            log.warn(
                    "API key authentication failed: {}",
                    e.getMessage()
            );

            SecurityContextHolder.clearContext();
            merchantContext.clear();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "code": 401,
                    "message": "%s"
                }
                """.formatted(e.getMessage()));

        } catch (Exception e) {

            // Other application errors
            log.error("API key authentication error", e);

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
    private boolean secretMatches(String rawSecret, ApiKey apiKey) {
        if (BCrypt.matches(rawSecret, apiKey.getKeySecretHash())) {
            return true;
        }
        boolean isGradePeriod = apiKey.getGracePeriodExpiresAt() != null && LocalDateTime.now().isBefore(apiKey.getGracePeriodExpiresAt());
        return isGradePeriod
                && apiKey.getPreviousKeySecretHash() != null
                && BCrypt.matches(rawSecret, apiKey.getPreviousKeySecretHash());
    }

    private String[] decodeHeader(String header) {

        String encoded = header.substring(BASIC_prefix.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(":");
        if (colon < 1) return null;

        return new String[]{decoded.substring(0, colon), decoded.substring(colon + 1)};
    }
}
