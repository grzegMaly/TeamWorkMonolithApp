package com.mordiniaa.backend.security.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNotNull(val);
        assertEquals(tokenId, Long.parseLong(val));
    }

    @Test
    @DisplayName("Rotate Token Test")
    void rotateTokenTest() {

        UUID sessionId = UUID.randomUUID();
        long tokenId = new Random().nextLong();

        sessionService.createSession(sessionId, tokenId);

        long newId = new Random().nextLong();
        sessionService.rotateRefreshToken(sessionId, newId);

        String val = redisTemplate.opsForValue().get("session:" + sessionId);
        assertNotNull(val);
        assertEquals(newId, Long.parseLong(val));
    }

    @Test
    @DisplayName("Delete Session Test")
    void deleteSessionTest() {

        UUID sessionId = UUID.randomUUID();
        long tokenId = new Random().nextLong();

        sessionService.createSession(sessionId, tokenId);

        sessionService.deleteSession(sessionId);

        assertThrows(RuntimeException.class,
                () -> sessionService.getTokenIdBySessionId(sessionId));
    }
}
