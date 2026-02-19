package com.mordiniaa.backend.events.user.events;

import java.util.UUID;

public record UserCreatedEvent(UUID userId) {
}
