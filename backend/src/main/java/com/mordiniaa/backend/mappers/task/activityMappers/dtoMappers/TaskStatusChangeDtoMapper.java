package com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers;

import com.mordiniaa.backend.dto.task.activity.TaskStatusChangeDto;
import com.mordiniaa.backend.mappers.user.UserRepresentationMapper;
import com.mordiniaa.backend.models.task.activity.TaskStatusChange;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusChangeDtoMapper extends AbstractActivityDtoMapper<TaskStatusChange, TaskStatusChangeDto> {

    public TaskStatusChangeDtoMapper(UserRepresentationMapper userRepresentationMapper) {
        super(userRepresentationMapper);
    }

    @Override
    protected TaskStatusChangeDto toTypedDto(TaskStatusChange element, UserRepresentation user) {

        TaskStatusChangeDto.TaskStatusChangeDtoBuilder<?, ?> builder =
                TaskStatusChangeDto.builder();
        mapBase(element, builder, user);
        return builder
                .prevStatus(element.getPrevStatus())
                .nextStatus(element.getNextStatus())
                .build();
    }

    @Override
    public Class<TaskStatusChange> getSupportedType() {
        return TaskStatusChange.class;
    }
}
