package com.mordiniaa.backend.mappers.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.dto.task.activity.TaskActivityElementDto;
import com.mordiniaa.backend.mappers.task.activityMappers.TaskActivityMapper;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final TaskActivityMapper taskActivityMapper;

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

    public TaskDetailsDTO toDetailedDto(Task task, Map<UUID, UserRepresentation> users) {
        TaskDetailsDTO dto = (TaskDetailsDTO) toShortenedDto(task);
        List<TaskActivityElementDto> elements = task.getActivityElements()
                .stream()
                .map(tElement -> {
                    UserRepresentation user = users.get(tElement.getUser());
                    return taskActivityMapper.toDto(tElement, user);
                })
                .sorted(Comparator.comparing(TaskActivityElementDto::getCreatedAt))
                .toList();
        dto.setTaskActivityElements(elements);
        return dto;
    }
}
