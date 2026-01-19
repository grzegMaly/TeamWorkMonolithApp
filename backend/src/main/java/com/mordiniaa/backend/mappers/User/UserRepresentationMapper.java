package com.mordiniaa.backend.mappers.User;

import com.mordiniaa.backend.dto.user.mongodb.UserDto;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class UserRepresentationMapper {

    public UserDto toDto(UserRepresentation userRepresentation) {
        return UserDto.builder()
                .userId(userRepresentation.getUserId())
                .username(userRepresentation.getUsername())
                .imageUrl(userRepresentation.getImageUrl())
                .build();
    }
}
