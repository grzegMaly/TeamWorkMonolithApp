package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.repositories.mysql.RefreshTokenFamilyRepository;
import com.mordiniaa.backend.repositories.mysql.RefreshTokenRepository;
import com.mordiniaa.backend.security.model.RefreshTokenEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.*;
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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        refreshTokenFamilyRepository.deleteAll();
    }

    @Test
    void generateRefreshTokenEntityTest() {

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

    @Test
    void rotateTokenValidTest() {

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

        Long savedFamilyId = entity.getRefreshTokenFamily().getId();

        Long tokenId = entity.getId();
        String newToken = rawTokenService.generateOpaqueToken();

        RefreshTokenEntity rotatedEntity = refreshTokenService.rotate(userId, tokenId, rawToken, newToken, roles);
        assertNotNull(rotatedEntity);
        assertEquals(roles, rotatedEntity.getRoles());

        RefreshTokenEntity revokedEntity = refreshTokenRepository.findById(tokenId)
                .orElseThrow();
        assertTrue(revokedEntity.isRevoked());
        assertTrue(revokedEntity.getRevokedAt().isBefore(Instant.now()));

        assertEquals(savedFamilyId, rotatedEntity.getRefreshTokenFamily().getId());
    }

    @Test
    void rotateRefreshTokenTokenNotFound() {

        UUID userId = UUID.randomUUID();
        Long tokenId = new Random().nextLong();
        String oldRawToken = rawTokenService.generateOpaqueToken();
        String newRawToken = rawTokenService.generateOpaqueToken();
        List<String> roles = List.of("ROLE_USER");

        assertThrows(RuntimeException.class,
                () -> refreshTokenService.rotate(userId, tokenId, oldRawToken, newRawToken, roles));
    }

    @Test
    void rotateTokenTokenRevoked() {

        UUID userId = UUID.randomUUID();
        Long familyId = new Random().nextLong();
        String rawToken = rawTokenService.generateOpaqueToken();
        List<String> roles = List.of("ROLE_USER");

        RefreshTokenEntity entity = refreshTokenService.generateRefreshTokenEntity(
                userId,
                familyId,
                rawToken,
                roles
        );

        entity.setRevoked(true);
        refreshTokenRepository.save(entity);

        String newToken = rawTokenService.generateOpaqueToken();
        assertThrows(RuntimeException.class,
                () -> refreshTokenService.rotate(userId, entity.getId(), rawToken, newToken, roles));
    }

    @Test
    void rotateTokenRawTokenMismatch() {
        UUID userId = UUID.randomUUID();
        Long familyId = new Random().nextLong();
        String rawToken = rawTokenService.generateOpaqueToken();
        List<String> roles = List.of("ROLE_USER");

        RefreshTokenEntity entity = refreshTokenService.generateRefreshTokenEntity(
                userId,
                familyId,
                rawToken,
                roles
        );

        Long tokenId = entity.getId();
        String newRawToken = rawTokenService.generateOpaqueToken();
        String newRawToke2 = rawTokenService.generateOpaqueToken();

        assertThrows(RuntimeException.class,
                () -> refreshTokenService.rotate(
                        userId,
                        tokenId,
                        newRawToken,
                        newRawToke2,
                        roles
                ));
    }
}
