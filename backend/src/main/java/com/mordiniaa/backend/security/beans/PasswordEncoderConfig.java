package com.mordiniaa.backend.security.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        String id = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(id, new BCryptPasswordEncoder(12));

        var delegating = new DelegatingPasswordEncoder(id, encoders);
        delegating.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder(12));
        return delegating;
    }
}
