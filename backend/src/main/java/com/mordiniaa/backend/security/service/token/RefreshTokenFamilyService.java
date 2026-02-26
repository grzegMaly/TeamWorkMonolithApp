package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.repositories.mysql.RefreshTokenFamilyRepository;
import com.mordiniaa.backend.security.model.RefreshTokenFamily;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenFamilyService {

    @Value("${security.app.refresh-token-family.max-session-days}")
    private int maxSessionDays;

    private final RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @Transactional
    public RefreshTokenFamily getRefreshTokenFamilyOrCreate(Long familyId, UUID userId) {
        return refreshTokenFamilyRepository.findRefreshTokenFamilyByIdAndUserIdAndRevokedFalse(familyId, userId)
                .orElseGet(() -> {
                    RefreshTokenFamily newFamily = new RefreshTokenFamily(userId);
                    newFamily.setExpiresAt(Instant.now().plus(Duration.ofDays(maxSessionDays)));
                    return refreshTokenFamilyRepository.save(newFamily);
                });
    }
}
