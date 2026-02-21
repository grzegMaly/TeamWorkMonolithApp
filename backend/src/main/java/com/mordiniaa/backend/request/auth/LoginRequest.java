package com.mordiniaa.backend.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    private String username;
    private String password;
}
