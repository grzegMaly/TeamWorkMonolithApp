package com.mordiniaa.backend.dto.task.activity;

import com.mordiniaa.backend.dto.user.mongodb.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TaskActivityElementDto {

    private UserDto user;
    private Instant createdAt;
}
