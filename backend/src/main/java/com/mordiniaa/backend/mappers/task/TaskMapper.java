package com.mordiniaa.backend.mappers.task;

import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.models.task.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskShortDto toShortenedDto(Task task) {

        TaskShortDto taskShortDto = new TaskShortDto();
        taskShortDto.setId(task.getId().toHexString());
        taskShortDto.setTitle(task.getTitle());
        taskShortDto.setDescription(task.getDescription());
        taskShortDto.setTaskStatus(task.getTaskStatus());
        taskShortDto.setAssignedTo(task.getAssignedTo());
        taskShortDto.setPositionInCategory(task.getPositionInCategory());
        taskShortDto.setDeadline(task.getDeadline());
        taskShortDto.setCreatedBy(task.getCreatedBy());
        return taskShortDto;
    }
}
