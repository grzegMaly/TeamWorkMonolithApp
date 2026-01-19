package com.mordiniaa.backend.dto.user.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private UUID userId;
    private String username;
    private String imageUrl;
}
