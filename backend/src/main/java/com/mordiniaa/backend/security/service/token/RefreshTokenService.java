package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.events.refreshToken.events.DeactivateTokenEvent;
import com.mordiniaa.backend.security.model.RefreshTokenFamily;
import com.mordiniaa.backend.security.token.RefreshToken;
import com.mordiniaa.backend.security.model.RefreshTokenEntity;
import com.mordiniaa.backend.repositories.mysql.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${security.app.refresh-token.token-name}")
    private String tokenName;

    @Value("${security.app.refresh-token.validity-days}")
    private int validityDays;

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenFamilyService refreshTokenFamilyService;

    @Transactional
    public RefreshTokenEntity generateRefreshTokenEntity(UUID userId, Long familyId, String rawToken, List<String> roles) {

        Instant now = Instant.now();

        RefreshTokenFamily family = refreshTokenFamilyService.getRefreshTokenFamilyOrCreate(familyId, userId);

        RefreshTokenEntity token = buildRefreshToken(family, now, null, rawToken, roles);
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshTokenEntity rotate(UUID userId, Long tokenId, String oldRawToken, String newRawToken, List<String> roles) {

        RefreshTokenEntity storedTokenEntity = getRefreshToken(tokenId);
        RefreshTokenFamily family = storedTokenEntity.getRefreshTokenFamily();

        Instant now = Instant.now();
        boolean tokenExpired = storedTokenEntity.getExpiresAt().isBefore(now);

        if (storedTokenEntity.isRevoked() || tokenExpired) {
            applicationEventPublisher.publishEvent(
                    new DeactivateTokenEvent(storedTokenEntity.getId(), family.getId(), now)
            );
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        if (!Objects.equals(family.getUserId(), userId))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        boolean familyExpired = family.getExpiresAt().isBefore(now);
        if (family.isRevoked() || familyExpired) {
            applicationEventPublisher.publishEvent(
                    new DeactivateTokenEvent(storedTokenEntity.getId(), family.getId(), now)
            );
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        boolean valid = MessageDigest.isEqual(
                sha256Bytes(oldRawToken),
                Base64.getUrlDecoder().decode(storedTokenEntity.getHashedToken())
        );

        if (!valid) {
            log.info("Invalid refresh token");
            throw new RuntimeException();
        }

        RefreshTokenEntity newTokenEntity = buildRefreshToken(family, now, storedTokenEntity.getId(), newRawToken, roles);
        RefreshTokenEntity savedEntity = refreshTokenRepository.save(newTokenEntity);

        rotateToken(savedEntity.getId(), storedTokenEntity.getId(), now);
        return savedEntity;
    }

    public RefreshToken generateRefreshToken(RefreshTokenEntity entity, String rawToken) {
        String storedRawToken = entity.getId() + "." + rawToken;
        return new RefreshToken(tokenName, storedRawToken, entity.getExpiresAt().toEpochMilli());
    }

    public RefreshTokenEntity getRefreshToken(Long tokenId) {
        return refreshTokenRepository.findById(tokenId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
    }

    @Transactional
    public void deactivateToken(Long tokenId, Long familyId, Instant revokedAt) {
        refreshTokenRepository.deactivateTokenWithFamily(tokenId, familyId, revokedAt);
    }

    @Transactional
    public void rotateToken(Long newTokenId, Long oldTokenId, Instant revokedAt) {
        refreshTokenRepository.rotateToken(newTokenId, oldTokenId, revokedAt);
    }

    private byte[] sha256Bytes(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(token.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private RefreshTokenEntity buildRefreshToken(RefreshTokenFamily family, Instant time, Long parentId, String rawToken, List<String> roles) {

        String hashed = Base64.getUrlEncoder().withoutPadding().encodeToString(sha256Bytes(rawToken));
        return RefreshTokenEntity.builder()
                .hashedToken(hashed)
                .refreshTokenFamily(family)
                .parentId(parentId)
                .roles(roles)
                .createdAt(time)
                .expiresAt(time.plus(Duration.ofDays(validityDays)))
                .build();
    }
}
