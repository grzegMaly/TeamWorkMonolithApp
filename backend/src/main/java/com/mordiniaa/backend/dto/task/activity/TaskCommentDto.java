package com.mordiniaa.backend.dto.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TaskCommentDto extends TaskActivityElementDto {

    private UUID commentId;
    private String comment;
    private boolean updated;
}
