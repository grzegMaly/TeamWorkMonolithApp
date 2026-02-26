package com.mordiniaa.backend.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final StringRedisTemplate redis;

    public void createSession(UUID sessionId, Long refreshTokenId) {

        String key = key(sessionId);

        redis.opsForValue().set(
                key,
                refreshTokenId.toString(),
                Duration.ofMinutes(30)
        );
    }

    private String key(UUID sessionId) {
        return "session:".concat(sessionId.toString());
    }
}
