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
        return fillBase(task, new TaskShortDto());
    }

    public TaskDetailsDTO toDetailedDto(Task task, Map<UUID, UserRepresentation> users) {
        TaskDetailsDTO dto = fillBase(task, new TaskDetailsDTO());
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

    private <T extends TaskShortDto> T fillBase(Task task, T dto) {
        dto.setId(task.getId().toHexString());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTaskStatus(task.getTaskStatus());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setPositionInCategory(task.getPositionInCategory());
        dto.setDeadline(task.getDeadline());
        dto.setCreatedBy(task.getCreatedBy());
        return dto;
    }
}
