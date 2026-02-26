package com.mordiniaa.backend.security.service;

import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.security.service.user.CustomUserDetailsPasswordService;
import com.mordiniaa.backend.security.service.user.SecurityUser;
import com.mordiniaa.backend.security.service.user.SecurityUserProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CustomUserDetailsPasswordServiceTest {

    @Autowired
    private CustomUserDetailsPasswordService passwordService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Test
    void updatePassword() {

        String newPassword = "CompletelyNewPassword";
        String hashedPassword = passwordEncoder.encode(newPassword);

        SecurityUserProjection projection = userRepository.findSecurityUserByUsername("admin")
                .orElseThrow();

        UserDetails userDetails = SecurityUser.build(projection);
        SecurityUser updatedUser = (SecurityUser) passwordService.updatePassword(userDetails, hashedPassword);

        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }
}
