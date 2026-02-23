package com.mordiniaa.backend.security.token.refreshToken;

import com.mordiniaa.backend.security.token.rawToken.RawToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${security.app.refresh-token.validity-days}")
    private int validityDays;

    @Value("${security.app.refresh-token.max-session-days}")
    private int maxSessionDays;

    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public RawToken rotate(Long tokenId, String rawToken) {
        return null;
    }

    @Transactional
    public RefreshToken generateRefreshToken(UUID userId, UUID familyId, String rawToken, Long parentId, List<String> roles) {

        Instant now = Instant.now();

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .hashedToken(hash(rawToken))
                .revoked(false)
                .parentId(parentId)
                .familyId(familyId)
                .roles(roles)
                .createdAt(now)
                .expiresAt(now.plus(Duration.ofDays(validityDays)))
                .familyExpiresAt(now.plus(Duration.ofDays(maxSessionDays)))
                .build();

        return refreshTokenRepository.save(token);
    }

    private String hash(String token) {
        return passwordEncoder.encode(token);
    }
}
