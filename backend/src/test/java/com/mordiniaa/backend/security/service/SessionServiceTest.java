package com.mordiniaa.backend.security.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class SessionServiceTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("Create Session Test")
    void createSessionTest() {

        UUID sessionId = UUID.randomUUID();
        long tokenId = new Random().nextLong();

        sessionService.createSession(sessionId, tokenId);

        String val = redisTemplate.opsForValue().get("session:" + sessionId);
        Assertions.assertNotNull(val);
        assertEquals(tokenId, Long.parseLong(val));
    }
}
