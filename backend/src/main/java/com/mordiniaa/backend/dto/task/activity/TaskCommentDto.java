package com.mordiniaa.backend.dto.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskCommentDto extends TaskActivityElementDto {

    private String comment;
    private boolean updated;
}
