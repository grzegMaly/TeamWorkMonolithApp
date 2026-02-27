package com.mordiniaa.backend.security.service.token;

import com.mordiniaa.backend.repositories.mysql.RefreshTokenFamilyRepository;
import com.mordiniaa.backend.repositories.mysql.RefreshTokenRepository;
import com.mordiniaa.backend.security.service.SessionService;
import com.mordiniaa.backend.security.token.JwtToken;
import com.mordiniaa.backend.security.token.RefreshToken;
import com.mordiniaa.backend.security.token.TokenSet;
import com.mordiniaa.backend.security.utils.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @Autowired
    private SessionService sessionService;
    @Autowired
    private JwtUtils jwtUtils;

    @AfterEach
    void tearDown() {
        refreshTokenFamilyRepository.deleteAll();
    }

    @Test
    @DisplayName("Issue Token Test")
    void issueTokenTest() {

        UUID userId = UUID.randomUUID();
        List<String> roles = List.of("ROLE_USER");

        TokenSet tokenSet = tokenService.issue(userId, roles);
        assertNotNull(tokenSet);

        JwtToken jwtToken = tokenSet.getJwtToken();
        RefreshToken refreshToken = tokenSet.getRefreshToken();

        assertNotNull(jwtToken);
        assertNotNull(refreshToken);

        assertNotNull(jwtToken.getToken());
        assertNotNull(jwtToken.getTokenName());
        assertTrue(jwtToken.getTtl() > 1);

        assertNotNull(refreshToken.getToken());
        assertNotNull(refreshToken.getTokenName());
        assertTrue(refreshToken.getTtl() > 1);

        String refreshTokenString = refreshToken.getToken();
        int idx = refreshTokenString.indexOf(".");
        String idPart = refreshTokenString.substring(0, idx);

        UUID sessionId = getSessionIdFromJwtToken(jwtToken.getToken());
        assertNotNull(sessionId);

        Long tokenIdFromRedis = sessionService.getTokenIdBySessionId(sessionId);
        assertNotNull(tokenIdFromRedis);
        assertEquals(Long.parseLong(idPart), tokenIdFromRedis);
    }

    private UUID getSessionIdFromJwtToken(String token) {
        String session = (String) Jwts.parser()
                .verifyWith((SecretKey) jwtUtils.key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("sid");

        try {
            return UUID.fromString(session);
        } catch (Exception e) {
            return null;
        }
    }
}
