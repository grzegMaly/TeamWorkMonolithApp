package com.mordiniaa.backend.security.beans;

import com.mordiniaa.backend.security.service.user.CustomUserDetailsPasswordService;
import com.mordiniaa.backend.security.service.user.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthProviderConfig {

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, CustomUserDetailsPasswordService customUserDetailsPasswordService, CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(true);
        provider.setUserDetailsPasswordService(customUserDetailsPasswordService);

        return provider;
    }
}
