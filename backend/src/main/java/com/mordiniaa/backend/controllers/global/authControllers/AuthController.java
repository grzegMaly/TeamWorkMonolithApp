package com.mordiniaa.backend.controllers.global.authControllers;

import com.mordiniaa.backend.dto.user.UserDto;
import com.mordiniaa.backend.response.user.UserInfoResponse;
import com.mordiniaa.backend.services.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/signin")
    public ResponseEntity<UserDto> login() {
        return null;
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserDetails() {
        UserInfoResponse infoResponse = authService.getUserDetails(UUID.randomUUID());
        return ResponseEntity.ok(infoResponse);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut() {

        return null;
    }
}
