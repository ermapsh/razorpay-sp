package com.ermapsh.razorpay.common.ratelimit.algo;

import com.ermapsh.razorpay.common.ratelimit.RateLimitResult;
import com.ermapsh.razorpay.common.ratelimit.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
@ConditionalOnProperty(
        name = "app.rate-limit.method",
        havingValue = "sliding-window"
)
public class SlidingWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate redis;

    @Override
    public RateLimitResult check(String keyId, int maxRequestAllowed, long windowSeconds) {

        long nowMs = System.currentTimeMillis();
        long floorMs = nowMs - (windowSeconds * 1000);

        String redisKey = "rate-limit:sliding" + keyId;

        var zSet = redis.opsForZSet();
        zSet.removeRangeByScore(redisKey, Double.NEGATIVE_INFINITY, floorMs); // so now all key between range

        Long count = zSet.zCard(redisKey);
        long current = count != null ? count : 0;

        if (current > maxRequestAllowed) {
            var oldest = zSet.rangeWithScores(redisKey, 0, 0);
            int retryAfter = 1;
            if (oldest != null && !oldest.isEmpty()) {
                Double oldestScore = oldest.iterator().next().getScore();
                if (oldestScore != null) {
                    long windowExpiresMs = oldestScore.longValue() + (windowSeconds * 1000);
                    retryAfter = (int) Math.ceil((windowExpiresMs - nowMs) / 1000.0);
                }
            }
            return RateLimitResult.denied(retryAfter);
        }

        zSet.add(redisKey, UUID.randomUUID().toString(), nowMs);
        redis.expire(redisKey, Duration.ofSeconds(windowSeconds + 1));
        return RateLimitResult.allowed((int) (maxRequestAllowed - current - 1));
    }
}
