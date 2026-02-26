package com.mordiniaa.backend.security.service;

import com.mordiniaa.backend.security.token.JwtToken;
import com.mordiniaa.backend.security.utils.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("Build Jwt Test")
    void buildJwtTest() {

        UUID testUserId = UUID.randomUUID();
        UUID testSessionId = UUID.randomUUID();
        JwtToken jwtToken = jwtService.buildJwt(testUserId.toString(), testSessionId.toString(), List.of("ROLE_ADMIN"));

        assertNotNull(jwtToken);

        assertNotNull(jwtToken.getToken());
        assertTrue(jwtToken.getTtl() > Duration.ofMinutes(14).toMillis());

        String token = jwtToken.getToken();
        assertNotNull(token);

        String subject = Jwts.parser().verifyWith((SecretKey) jwtUtils.key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();

        assertEquals(testUserId.toString(), subject);
    }
}
