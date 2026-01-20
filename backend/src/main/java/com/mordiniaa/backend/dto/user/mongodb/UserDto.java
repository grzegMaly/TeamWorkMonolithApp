package com.mordiniaa.backend.dto.user.mongodb;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID userId;
    private String username;
    private String imageUrl;
}
