package com.rahul.fluxlimit.service;

import com.rahul.fluxlimit.model.RateLimitResult;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class RateLimitingService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private RedisScript<List> redisScript;

    @PostConstruct
    public void init() {
        redisScript = RedisScript.of(new ClassPathResource("rate_limiter.lua"), List.class);
    }

    public RateLimitResult checkLimit(String clientId, int capacity, int refillRate) {
        List<String> keys = Collections.singletonList("rate_limit:" + clientId);
        long currentTime = Instant.now().getEpochSecond();

        List<Long> result = redisTemplate.execute(
                redisScript,
                keys,
                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(currentTime),
                "1"
        );

        if (result == null || result.isEmpty()) {
            return new RateLimitResult(false, 0);
        }

        boolean allowed = result.get(0) == 1L;
        long remainingTokens = result.get(1);

        return new RateLimitResult(allowed, remainingTokens);
    }
}
