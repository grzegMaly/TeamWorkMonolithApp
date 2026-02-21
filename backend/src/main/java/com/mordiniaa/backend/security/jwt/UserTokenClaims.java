package com.mordiniaa.backend.security.jwt;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserTokenClaims(UUID userId, String username, String tokenName,
                              long tokenVersion, List<String> roles, List<String> amr) {
}
