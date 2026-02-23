package com.mordiniaa.backend.services.auth;

import com.mordiniaa.backend.mappers.user.UserMapper;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.request.auth.LoginRequest;
import com.mordiniaa.backend.response.user.UserInfoResponse;
import com.mordiniaa.backend.security.service.JwtService;
import com.mordiniaa.backend.security.token.rawToken.RawTokenService;
import com.mordiniaa.backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RawTokenService rawTokenService;
    private final JwtService jwtService;

    @Transactional
    public UserInfoResponse getUserDetails(UUID userId) {

        User user = userService.getUser(userId);
        List<String> roles = List.of(user.getRole().getAppRole().toString()); // TODO: Change In Security

        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getContact().getEmail())
                .accountNonLocked(user.isAccountNonLocked())
                .accountNonExpired(user.isAccountNonExpired())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .deleted(user.isDeleted())
                .credentialsExpiryDate(user.getCredentialsExpiryDate())
                .accountExpiryDate(user.getAccountExpiryDate())
                .roles(roles)
                .build();
    }
}
