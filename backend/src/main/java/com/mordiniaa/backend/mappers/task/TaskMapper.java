package com.mordiniaa.backend.mappers.task;

import com.mordiniaa.backend.dto.task.TaskCardDto;
import com.mordiniaa.backend.models.task.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskCardDto toShortenedDto(Task task) {

        TaskCardDto taskCardDto = new TaskCardDto();
        taskCardDto.setId(task.getId().toHexString());
        taskCardDto.setTitle(task.getTitle());
        taskCardDto.setDescription(task.getDescription());
        taskCardDto.setTaskStatus(task.getTaskStatus());
        taskCardDto.setAssignedTo(task.getAssignedTo());
        taskCardDto.setPositionInCategory(task.getPositionInCategory());
        taskCardDto.setDeadline(task.getDeadline());
        taskCardDto.setCreatedBy(task.getCreatedBy());
        return taskCardDto;
    }
}
