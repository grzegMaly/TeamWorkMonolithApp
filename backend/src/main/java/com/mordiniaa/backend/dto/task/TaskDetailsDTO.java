package com.mordiniaa.backend.dto.task;

import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class TaskDetailsDTO extends TaskShortDto {

    private Set<TaskActivityElement> taskActivityElements;
}
