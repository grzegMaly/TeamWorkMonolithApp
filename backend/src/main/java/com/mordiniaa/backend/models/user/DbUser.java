package com.mordiniaa.backend.models.user;

import java.util.UUID;

public interface DbUser {

    UUID getUserId();

    String getUsername();

    String getImageKey();
}
