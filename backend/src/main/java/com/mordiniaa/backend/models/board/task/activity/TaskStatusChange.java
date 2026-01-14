package com.mordiniaa.backend.models.board.task.activity;

import com.mordiniaa.backend.models.board.task.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("status_change")
@NoArgsConstructor
public class TaskStatusChange extends TaskActivityElement {

    private TaskStatus prevStaus;
    private TaskStatus nextStatus;
}
