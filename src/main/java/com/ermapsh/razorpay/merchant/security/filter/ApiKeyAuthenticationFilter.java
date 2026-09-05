package com.ermapsh.razorpay.merchant.security.filter;

import com.ermapsh.razorpay.common.exception.RateLimiterException;
import com.ermapsh.razorpay.common.ratelimit.RateLimitResult;
import com.ermapsh.razorpay.common.ratelimit.RateLimiter;
import com.ermapsh.razorpay.merchant.cache.ApiKeyCache;
import com.ermapsh.razorpay.merchant.cache.ApiKeyCacheEntry;
import com.ermapsh.razorpay.merchant.entity.ApiKey;
import com.ermapsh.razorpay.merchant.repository.ApiKeyRepository;
import com.ermapsh.razorpay.merchant.security.MerchantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.rate-limit.use-case.api-key.requests-per-minute}")
    private Integer requestPerMin;

    private static final String BASIC_PREFIX = "Basic ";

    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder BCrypt = new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final ApiKeyCache apiKeyCache;
    private final RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request: {}", request.getRequestURI());

        try {

            String authorizationHeader = request.getHeader("Authorization");

            // Same idea as JwtAuthenticationFilter:
            // If this request doesn't contain Basic Auth,
            // let the request continue.
            if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Decode keyId:secret
            String[] credentials = decodeHeader(authorizationHeader);

            if (credentials == null) throw new BadCredentialsException("Malformed API key header");

            String keyId = credentials[0];
            String secretKey = credentials[1];

            ApiKeyCacheEntry apiKeyEntry = apiKeyCache.get(keyId).orElseGet(() -> loadAndCache(keyId));

            // Check enabled
            if (apiKeyEntry == null || !apiKeyEntry.enabled() || !secretMatches(secretKey, apiKeyEntry))
                throw new BadCredentialsException("API key is disabled");

            RateLimitResult rateLimitResult = rateLimiter.check("apiKey:"+keyId, requestPerMin, 60 );

            if (!rateLimitResult.isAllowed()) {
                log.warn("Too many requests for keyId: {}", keyId);
                response.setHeader(
                        "Retry-After",
                        String.valueOf(rateLimitResult.retryAfterSeconds())
                );

                throw new RateLimiterException(
                        "Too many requests. Please try again later.",
                        rateLimitResult.retryAfterSeconds()
                );
            }

            response.setHeader("X-RateLimit", String.valueOf(requestPerMin));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitResult.remaining()));

            // Check secret
            if (!secretMatches(secretKey, apiKeyEntry)) throw new BadCredentialsException("Invalid API key");

            // Create authentication
            var auth = new UsernamePasswordAuthenticationToken(keyId, null, List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Set MerchantContext
            merchantContext.setMerchantId(apiKeyEntry.merchantId());
            merchantContext.setKeyId(apiKeyEntry.keyId());

            log.info("API key authenticated successfully. keyId={}, merchantId={}", apiKeyEntry.keyId(), apiKeyEntry.merchantId());

            // Continue
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            log.error("API key authentication failed: {}", e.getMessage());

            SecurityContextHolder.clearContext();
            merchantContext.clear();

            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private ApiKeyCacheEntry loadAndCache(String keyId) {
        // Find API key
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId).orElse(null);
        if (apiKey == null) return null;
        ApiKeyCacheEntry apiKeyCacheEntry = new ApiKeyCacheEntry(
                apiKey.getMerchant().getId(),
                apiKey.getKeyId(),
                apiKey.getKeySecretHash(),
                apiKey.getPreviousKeySecretHash(),
                apiKey.getEnvironment(),
                apiKey.isEnabled(),
                apiKey.getRotatedAt(),
                apiKey.getGracePeriodExpiresAt()
        );
        apiKeyCache.put(apiKey.getKeyId(), apiKeyCacheEntry);
        return apiKeyCacheEntry;
    }

    private boolean secretMatches(String rawSecret, ApiKeyCacheEntry apiKeyEntry) {
        if (BCrypt.matches(rawSecret, apiKeyEntry.keySecretHash())) return true;
        return apiKeyEntry.isInGracePeriod() &&
               apiKeyEntry.previousKeySecretHash() != null
               && BCrypt.matches(rawSecret, apiKeyEntry.previousKeySecretHash());
    }

    private String[] decodeHeader(String header) {

        try {

            String encoded = header.substring(BASIC_PREFIX.length());

            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

            int colon = decoded.indexOf(":");

            if (colon <= 0) {
                return null;
            }

            String keyId = decoded.substring(0, colon);

            String secretKey = decoded.substring(colon + 1);

            if (secretKey.isBlank()) {
                return null;
            }

            return new String[]{keyId, secretKey};

        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}