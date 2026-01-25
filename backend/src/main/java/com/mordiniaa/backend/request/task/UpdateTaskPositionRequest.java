package com.mordiniaa.backend.request.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTaskPositionRequest {

    @NotNull(message = "Task Position Must Be Specified")
    @PositiveOrZero(message = "Task Position Cannot Be Negative")
    private int newPosition;

    private String newTaskCategory;
}
