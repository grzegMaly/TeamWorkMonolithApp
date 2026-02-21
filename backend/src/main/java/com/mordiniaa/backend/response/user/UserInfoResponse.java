package com.mordiniaa.backend.response.user;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private UUID userId;
    private String username;
    private String email;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean deleted;
    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private List<String> roles;
}
