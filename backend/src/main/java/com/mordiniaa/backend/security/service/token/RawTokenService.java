package com.mordiniaa.backend.security.service.token;


import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class RawTokenService {

    private final SecureRandom SR = new SecureRandom();

    public String generateOpaqueToken() {
        byte[] b = new byte[32];
        SR.nextBytes(b);
        return Base64.getUrlEncoder().encodeToString(b);
    }
}
