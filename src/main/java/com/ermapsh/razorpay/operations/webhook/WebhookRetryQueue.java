package com.ermapsh.razorpay.operations.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookRetryQueue {

    private final RedisTemplate redisTemplate;

    @Value("${app.webhook.delivery.redis-key")
    private String key;

    public void enqueue(UUID webhookEventId, LocalDateTime retryAt) {
        long time = retryAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        redisTemplate.opsForZSet().add(key, webhookEventId.toString(), time);
    }

    public Set<UUID> pollDue(int limit) {
        long now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        Set<ZSetOperations.TypedTuple<String>> dues = redisTemplate.opsForZSet().
                rangeByScoreWithScores(key, 0, now, 0, limit);
        if (dues == null || dues.isEmpty()) return Set.of();

        dues.forEach(tuple -> redisTemplate.opsForZSet().remove(key,
                tuple.getValue()));

        return dues.stream().
                map(tuple -> UUID.fromString(tuple.getValue())).
                collect(Collectors.toSet());
    }

    public void enqueueIfAbsent(UUID id, LocalDateTime nextRetryAt) {
        redisTemplate.opsForZSet().addIfAbsent(key, id.toString(), getTime(nextRetryAt));
    }

    private static long getTime(LocalDateTime retryAt) {
        long time = retryAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        return time;
    }
}
