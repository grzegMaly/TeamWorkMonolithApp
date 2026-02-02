package com.mordiniaa.backend.dto.task.activity;

import com.mordiniaa.backend.dto.user.mongodb.MongoUserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TaskActivityElementDto {

    private MongoUserDto user;
    private Instant createdAt;
}
