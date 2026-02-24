package com.mordiniaa.backend.services.auth;

import com.mordiniaa.backend.mappers.user.UserMapper;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.request.auth.LoginRequest;
import com.mordiniaa.backend.response.user.UserInfoResponse;
import com.mordiniaa.backend.security.service.JwtService;
import com.mordiniaa.backend.security.service.token.RawTokenService;
import com.mordiniaa.backend.security.service.token.TokenIssuerService;
import com.mordiniaa.backend.security.service.user.SecurityUserProjection;
import com.mordiniaa.backend.security.token.JwtToken;
import com.mordiniaa.backend.security.token.RefreshToken;
import com.mordiniaa.backend.security.token.TokenSet;
import com.mordiniaa.backend.security.utils.JwtUtils;
import com.mordiniaa.backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenIssuerService tokenIssuerService;
    @Value("${security.app.jwt.cookie-name}")
    private String jwtCookieName;

    @Value("${security.app.refresh-token.cookie-name}")
    private String refreshCookieName;

    private final UserService userService;
    private final JwtUtils jwtUtils;

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

    public List<ResponseCookie> authenticate(Authentication authentication) {

        SecurityUserProjection user = (SecurityUserProjection) authentication.getPrincipal();

        TokenSet tokenSet = tokenIssuerService.issue(user.getUserId(), List.of(user.getRole().getAppRole().name()));

        JwtToken jwtToken = tokenSet.getJwtToken();
        ResponseCookie jwtCookie = jwtUtils.getCookie(
                jwtCookieName,
                jwtToken.getJwtToken(),
                Duration.ofMillis(jwtToken.getJwtTtl())
        );

        RefreshToken refreshToken = tokenSet.getRefreshToken();
        ResponseCookie responseCookie = jwtUtils.getCookie(
                refreshCookieName,
                refreshToken.getRefreshToken(),
                Duration.ofMillis(refreshToken.getRefreshTtl())
        );

        return List.of(jwtCookie, responseCookie);
    }
}
