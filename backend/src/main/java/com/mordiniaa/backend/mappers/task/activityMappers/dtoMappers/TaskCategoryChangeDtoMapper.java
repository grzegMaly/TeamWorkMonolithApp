package com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers;

import com.mordiniaa.backend.dto.task.activity.TaskCategoryChangeDto;
import com.mordiniaa.backend.mappers.user.UserMapper;
import com.mordiniaa.backend.models.task.activity.TaskCategoryChange;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class TaskCategoryChangeDtoMapper extends AbstractActivityDtoMapper<TaskCategoryChange, TaskCategoryChangeDto> {

    public TaskCategoryChangeDtoMapper(UserMapper userMapper) {
        super(userMapper);
    }

    @Override
    protected TaskCategoryChangeDto toTypedDto(TaskCategoryChange element, UserRepresentation user) {

        TaskCategoryChangeDto.TaskCategoryChangeDtoBuilder<?, ?> builder =
                TaskCategoryChangeDto.builder();
        mapBase(element, builder, user);
        return builder
                .prevTaskCategoryName(element.getPrevCategory())
                .nextTaskCategoryName(element.getNextCategory())
                .build();
    }

    @Override
    public Class<TaskCategoryChange> getSupportedType() {
        return TaskCategoryChange.class;
    }
}
