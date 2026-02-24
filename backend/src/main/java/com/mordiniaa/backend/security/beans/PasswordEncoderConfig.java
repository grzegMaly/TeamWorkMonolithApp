package com.mordiniaa.backend.security.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder bcryptEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        String defaultId = "argon2";

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder(12));
        encoders.put("argon2", new Argon2PasswordEncoder(24, 32, 1, 65536, 3));

        DelegatingPasswordEncoder delegating = new DelegatingPasswordEncoder(defaultId, encoders);
        delegating.setDefaultPasswordEncoderForMatches(encoders.get("bcrypt"));

        return delegating;
    }
}
