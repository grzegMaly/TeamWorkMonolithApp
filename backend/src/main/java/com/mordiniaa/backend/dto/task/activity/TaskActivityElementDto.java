package com.mordiniaa.backend.dto.task.activity;

import com.mordiniaa.backend.dto.user.mongodb.UserProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TaskActivityElementDTO {

    private UserProjection user;
    private Instant createdAt;
}
