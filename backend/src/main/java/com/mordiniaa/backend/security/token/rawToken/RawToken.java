package com.mordiniaa.backend.security.token.rawToken;

public record RawToken(Long id, String token, long expiresAt, String familyId) {
}
