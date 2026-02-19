package com.mordiniaa.backend.events.user.events;

import java.util.UUID;

public record UserUsernameChangedEvent(UUID userId, String username) {
}
