package com.mordiniaa.backend.dto.task.activity;

import com.mordiniaa.backend.models.task.TaskStatus;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("status_change")
@NoArgsConstructor
public class TaskStatusChangeDto extends TaskActivityElement {

    private TaskStatus prevStatus;
    private TaskStatus nextStatus;
}
