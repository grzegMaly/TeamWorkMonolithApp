package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.repositories.mysql.RefreshTokenFamilyRepository;
import com.mordiniaa.backend.security.model.RefreshTokenFamily;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RefreshTokenFamilyServiceTest {

    @Autowired
    private RefreshTokenFamilyService refreshTokenFamilyService;

    @Autowired
    private RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @AfterEach
    void tearDown() {
        refreshTokenFamilyRepository.deleteAll();
    }

    @Test
    void getRefreshTokenFamilyOrCreateTest() {

        Long familyId = new Random().nextLong();
        UUID userId = UUID.randomUUID();

        RefreshTokenFamily newFamily = refreshTokenFamilyService.getRefreshTokenFamilyOrCreate(familyId, userId);
        assertNotNull(newFamily);

        assertEquals(userId, newFamily.getUserId());

        Long savedFamilyId = newFamily.getId();
        assertEquals(1, savedFamilyId);
        assertNotEquals(familyId, savedFamilyId);

        RefreshTokenFamily savedFamily = refreshTokenFamilyService.getRefreshTokenFamilyOrCreate(savedFamilyId, userId);
        assertNotNull(savedFamily);
        assertEquals(userId, savedFamily.getUserId());
        assertEquals(savedFamilyId, savedFamily.getId());

        assertTrue(savedFamily.getExpiresAt().toEpochMilli() > Instant.now().plus(Duration.ofDays(89)).toEpochMilli());
    }
}

