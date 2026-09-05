package com.ermapsh.razorpay.common.ratelimit.algo;

import com.ermapsh.razorpay.common.ratelimit.RateLimitResult;
import com.ermapsh.razorpay.common.ratelimit.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
@ConditionalOnProperty(
        name = "app.rate-limit.method",
        havingValue = "fixed"
)
public class FixedWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate redis;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {
        String redisKey = "rate-limit:fixed:" + key;
        Long count = redis.opsForValue().increment(redisKey);
        if (count == null) return RateLimitResult.allowed(maxRequestAllowed); // NOTE: redis not available

        if (count == 1) {
            redis.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }

        if (count > maxRequestAllowed) {
            Long ttl = redis.getExpire(redisKey, TimeUnit.SECONDS);
            int retryAfter = (ttl != null && ttl > 0) ? ttl.intValue() : (int) windowSeconds;
            return RateLimitResult.denied(retryAfter);
        }

        return RateLimitResult.allowed((int) (maxRequestAllowed - count));
    }
}
