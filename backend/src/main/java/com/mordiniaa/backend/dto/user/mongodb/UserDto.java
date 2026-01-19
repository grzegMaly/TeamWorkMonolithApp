package com.mordiniaa.backend.dto.user.mongodb;

import java.util.UUID;

public interface UserProjection {

    UUID getUserId();

    String getUsername();

    String getImageUrl();
}
