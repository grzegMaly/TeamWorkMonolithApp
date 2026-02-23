package com.mordiniaa.backend.security.service.user;

import com.mordiniaa.backend.models.user.mysql.Role;

import java.util.UUID;

public interface SecurityUserProjection {

    UUID getUserId();

    String getUsername();

    String getPassword();

    Role getRole();

    boolean getAccountNonExpired();

    boolean getAccountNonLocked();

    boolean getCredentialsNonExpired();

    boolean getDeleted();
}
