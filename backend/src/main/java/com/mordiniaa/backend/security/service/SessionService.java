package com.mordiniaa.backend.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final StringRedisTemplate redis;

    public void createSession(String sessionId, Long refreshTokenId) {

        String key = "session:".concat(sessionId);

        redis.opsForValue().set(
                key,
                refreshTokenId.toString(),
                Duration.ofMinutes(30)
        );
    }

    public void validateRefreshToken() {

    }

    public void rotateRefreshToken() {

    }

    public void deleteSession() {

    }
}
