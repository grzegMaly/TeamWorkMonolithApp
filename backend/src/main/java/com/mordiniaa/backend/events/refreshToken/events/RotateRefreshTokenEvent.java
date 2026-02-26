package com.mordiniaa.backend.events.refreshToken.events;


import java.time.Instant;

public record RotateRefreshTokenEvent(Long newTokenId, Long oldTokenId, Instant revokedTime) {
}
