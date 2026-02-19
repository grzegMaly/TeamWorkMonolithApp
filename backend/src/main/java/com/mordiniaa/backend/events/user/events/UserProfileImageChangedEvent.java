package com.mordiniaa.backend.events.user.events;

import java.util.UUID;

public record UserProfileImageChangedEvent(UUID userId, String imageKey) {
}
