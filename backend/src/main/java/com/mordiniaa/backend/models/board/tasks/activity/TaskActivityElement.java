package com.mordiniaa.backend.models.board.tasks.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TaskActivityElement {

    private UUID userId;
    private Instant createdAt;
}
