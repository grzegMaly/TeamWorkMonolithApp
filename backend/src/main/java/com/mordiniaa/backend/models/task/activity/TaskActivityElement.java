package com.mordiniaa.backend.models.task.activity;

import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TaskActivityElement {

    private UserRepresentation user;
    private Instant createdAt;
}
