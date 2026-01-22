package com.mordiniaa.backend.models.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

import java.util.UUID;

@Getter
@Setter
@TypeAlias("comment")
@NoArgsConstructor
public class TaskComment extends TaskActivityElement {

    private UUID commentId;
    private String comment;
    private boolean updated = false;
}
