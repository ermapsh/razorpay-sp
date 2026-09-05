package com.ermapsh.razorpay.common.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisIdempotencyStore implements IdempotencyStore {

    private static final String PREFIX = "idempotency";
    private final StringRedisTemplate redis;

    @Override
    public boolean setIfAbsent(String key, Duration ttl) {
        try {
            Boolean set = redis.opsForValue().setIfAbsent(PREFIX + key, IN_PROGRESS, ttl);
            return Boolean.TRUE.equals(set);
        } catch (DataAccessException e) {
            log.info("Idempotency store unavailable, failing open for keys={}", key, e);
            return false;
        }
    }

    @Override
    public void store(String key, String value, Duration ttl) {
        try {
            redis.opsForValue().set(PREFIX + key, value, ttl);
        } catch (DataAccessException e) {
            log.info("Failed to persist, failing open for keys={}", key, e);
        }
    }

    @Override
    public Optional<String> get(String key) {
        try {
            return Optional.ofNullable(redis.opsForValue().get(PREFIX + key));
        } catch (DataAccessException e) {
            log.info("Failed to persist, failing open for keys={}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        try {
            redis.delete(PREFIX + key);
        } catch (DataAccessException e) {
            log.info("Failed to delete, failing open for keys={}", key, e);
        }
    }
}
