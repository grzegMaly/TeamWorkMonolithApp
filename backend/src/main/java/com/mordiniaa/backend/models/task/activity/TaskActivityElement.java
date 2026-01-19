package com.mordiniaa.backend.models.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public abstract class TaskActivityElement {

    private UUID user;
    private Instant createdAt;
}
