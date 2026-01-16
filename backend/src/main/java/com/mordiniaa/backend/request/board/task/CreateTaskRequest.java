package com.mordiniaa.backend.request.board.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@NotNull
public class CreateTaskRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String title;

    @NotBlank
    @Size(min = 3, max = 512)
    private String description;

    private Set<UUID> assignedTo; //Optional

    @NotNull
    private Instant deadline;
}
