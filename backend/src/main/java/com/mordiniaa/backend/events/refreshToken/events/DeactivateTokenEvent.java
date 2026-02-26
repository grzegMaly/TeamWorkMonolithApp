package com.mordiniaa.backend.events.refreshToken.events;

import java.time.Instant;

public record DeactivateTokenEvent(Long tokenId, Long familyId, Instant revokeTime) {
}
