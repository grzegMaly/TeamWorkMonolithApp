package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.repositories.mysql.RefreshTokenFamilyRepository;
import com.mordiniaa.backend.security.model.RefreshTokenEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenFamilyRepository refreshTokenFamilyRepository;
    @Autowired
    private RawTokenService rawTokenService;

    @AfterEach
    void tearDown() {
        refreshTokenFamilyRepository.deleteAll();
    }

    @Test
    void generateRefreshTokenEntity() {

        UUID userId = UUID.randomUUID();
        Long familyId = new Random().nextLong();
        String rawToken = rawTokenService.generateOpaqueToken();
        List<String> roles = List.of("ROLE_ADMIN");

        RefreshTokenEntity entity = refreshTokenService.generateRefreshTokenEntity(
                userId,
                familyId,
                rawToken,
                roles
        );

        assertNotNull(entity);
        assertFalse(entity.isRevoked());
        assertTrue(entity.getExpiresAt().toEpochMilli() > Instant.now().toEpochMilli());
    }
}
