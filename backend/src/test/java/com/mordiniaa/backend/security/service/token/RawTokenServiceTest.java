package com.mordiniaa.backend.security.service.token;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class RawTokenServiceTest {

    @Autowired
    private RawTokenService rawTokenService;

    @Test
    void createRawTokenTest() {

        String rawToken = rawTokenService.generateOpaqueToken();
        assertNotNull(rawToken);
        System.out.println(rawToken);
    }
}
