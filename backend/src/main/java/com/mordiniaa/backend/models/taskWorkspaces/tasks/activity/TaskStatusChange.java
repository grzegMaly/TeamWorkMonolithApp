package com.mordiniaa.backend.models.taskWorkspaces.tasks.activity;

import com.mordiniaa.backend.models.taskWorkspaces.tasks.TaskStatus;
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
