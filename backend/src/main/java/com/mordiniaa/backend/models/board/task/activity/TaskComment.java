package com.mordiniaa.backend.models.board.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("comment")
@NoArgsConstructor
public class TaskComment extends TaskActivityElement {

    private String comment;
    private boolean updated = false;
}
