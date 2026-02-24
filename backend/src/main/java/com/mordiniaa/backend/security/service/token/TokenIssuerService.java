package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.security.model.RefreshTokenEntity;
import com.mordiniaa.backend.security.service.JwtService;
import com.mordiniaa.backend.security.service.SessionService;
import com.mordiniaa.backend.security.token.JwtToken;
import com.mordiniaa.backend.security.token.RefreshToken;
import com.mordiniaa.backend.security.token.TokenSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenIssuerService {

    private final RawTokenService rawTokenService;
    private final RefreshTokenService refreshTokenService;
    private final SessionService sessionService;
    private final JwtService jwtService;

    public TokenSet issue(UUID userId, List<String> roles) {

        String rawToken = rawTokenService.generateOpaqueToken();

        RefreshTokenEntity entity = refreshTokenService.generateRefreshToken(
                userId,
                UUID.randomUUID(),
                rawToken,
                null,
                roles
        );

        RefreshToken refreshToken = new RefreshToken(rawToken, entity.getExpiresAt().toEpochMilli());

        UUID sessionId = UUID.randomUUID();
        sessionService.createSession(
                sessionId,
                entity.getId()
        );

        JwtToken jwtToken = jwtService.buildJwt(
                userId.toString(),
                sessionId.toString(),
                roles
        );

        return new TokenSet(jwtToken, refreshToken);
    }
}
