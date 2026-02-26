package com.mordiniaa.backend.security.service;

import com.mordiniaa.backend.security.token.JwtToken;
import com.mordiniaa.backend.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.app.jwt.token-name}")
    private String tokenName;

    @Value("${security.app.jwt.minutes-of-life}")
    private long accessTtlMinutes;

    @Value("${security.app.jwt.issuer}")
    private String issuer;

    @Value("${security.app.jwt.audience}")
    private String audience;

    private final JwtUtils jwtUtils;

    public boolean validateToke(String jwtToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) jwtUtils.key())
                    .build().parseSignedClaims(jwtToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is not supported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims starting is empty: {}", e.getMessage());
        }
        return false;
    }

    public JwtToken buildJwt(String subject, String sessionId, List<String> roles) {

        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(accessTtlMinutes));

        String role = (roles == null || roles.isEmpty()) ? null : roles.getFirst();

        String jwt = Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))

                .claim("role", role)
                .claim("sid", sessionId)
                .signWith(jwtUtils.key())
                .compact();

        return new JwtToken(tokenName, jwt, exp.toEpochMilli());
    }

    public String extractUserId(String authToken) {
//        return Jwts.parser()
//                .verifyWith((SecretKey) jwtUtils.key())
//                .build()
//                .parseSignedClaims(authToken)
//                .getPayload()
        return null;
    }

    public String extractSessionId(String authToken) {

        return null;
    }

    public List<String> extractRoles(String authToken) {

        return null;
    }
}
