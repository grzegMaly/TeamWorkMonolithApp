package com.mordiniaa.backend.dto.task;

import com.mordiniaa.backend.models.task.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TaskShortDto {

    private String id;
    private int positionInCategory;
    private String title;
    private String description;
    private TaskStatus taskStatus;
    private UUID createdBy;
    private Set<UUID> assignedTo;
    private Instant deadline;
}
