package com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers;

import com.mordiniaa.backend.dto.task.activity.TaskCommentDto;
import com.mordiniaa.backend.mappers.user.UserMapper;
import com.mordiniaa.backend.models.task.activity.TaskComment;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class TaskCommentDtoMapper extends AbstractActivityDtoMapper<TaskComment, TaskCommentDto> {

    public TaskCommentDtoMapper(UserMapper userMapper) {
        super(userMapper);
    }

    @Override
    protected TaskCommentDto toTypedDto(TaskComment element, UserRepresentation user) {
        TaskCommentDto.TaskCommentDtoBuilder<?, ?> builder =
                TaskCommentDto.builder();

        mapBase(element, builder, user);
        return builder
                .commentId(element.getCommentId())
                .comment(element.getComment().trim())
                .updated(element.isUpdated())
                .build();
    }

    @Override
    public Class<TaskComment> getSupportedType() {
        return TaskComment.class;
    }
}
