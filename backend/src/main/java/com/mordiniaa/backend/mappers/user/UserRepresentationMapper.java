package com.mordiniaa.backend.mappers.user;

import com.mordiniaa.backend.dto.user.mongodb.MongoUserDto;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class UserRepresentationMapper {

    public MongoUserDto toDto(UserRepresentation userRepresentation) {
        return MongoUserDto.builder()
                .userId(userRepresentation.getUserId())
                .username(userRepresentation.getUsername())
                .imageUrl(userRepresentation.getImageUrl())
                .build();
    }
}
