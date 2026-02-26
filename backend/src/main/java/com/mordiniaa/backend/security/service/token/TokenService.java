package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.security.model.RefreshTokenEntity;
import com.mordiniaa.backend.security.service.JwtService;
import com.mordiniaa.backend.security.service.SessionService;
import com.mordiniaa.backend.security.token.JwtToken;
import com.mordiniaa.backend.security.token.RefreshToken;
import com.mordiniaa.backend.security.token.TokenSet;
import com.mordiniaa.backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RawTokenService rawTokenService;
    private final RefreshTokenService refreshTokenService;
    private final SessionService sessionService;
    private final JwtService jwtService;
    private final UserService userService;

    public TokenSet issue(UUID userId, List<String> roles) {

        String rawToken = rawTokenService.generateOpaqueToken();

        RefreshTokenEntity entity = refreshTokenService.generateRefreshTokenEntity(
                userId,
                null,
                rawToken,
                roles
        );

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(entity, rawToken);

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

    @Transactional
    public TokenSet refreshToken(UUID userId, UUID sessionId, String oldRefreshToken) {

        int dotIdx = oldRefreshToken.indexOf("\\.");
        if (dotIdx < 1) throw new RuntimeException(); // TODO: Change in exceptions Section

        String idPart = oldRefreshToken.substring(0, dotIdx);
        String tokenPart = oldRefreshToken.substring(dotIdx + 1);

        long tokenId;
        try {
            tokenId = Long.parseLong(idPart);
        } catch (NumberFormatException e) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        long storedTokenId = sessionService.getTokenIdBySessionId(sessionId);
        if (tokenId != storedTokenId)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        User user = userService.getUser(userId);
        List<String> roles = List.of(user.getRole().getAppRole().toString());

        String newRawToken = rawTokenService.generateOpaqueToken();
        RefreshTokenEntity storedEntity;
        try {
            storedEntity = refreshTokenService.rotate(userId, tokenId, tokenPart, newRawToken, roles);
        } catch (Exception e) {
            sessionService.deleteSession(sessionId);
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(storedEntity, newRawToken);

        sessionService.rotateRefreshToken(
                sessionId,
                storedEntity.getId()
        );

        JwtToken jwtToken = jwtService.buildJwt(
                userId.toString(),
                sessionId.toString(),
                roles
        );

        return new TokenSet(jwtToken, refreshToken);
    }
}
