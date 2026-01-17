package com.mordiniaa.backend.request.board.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class PatchTaskDataRequest {

    private String title;
    private String description;
    private Instant deadline;
}
