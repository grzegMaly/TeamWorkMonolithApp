package com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers;

import com.mordiniaa.backend.dto.task.activity.TaskActivityElementDto;
import com.mordiniaa.backend.dto.user.mongodb.MongoUserDto;
import com.mordiniaa.backend.mappers.user.UserRepresentationMapper;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractActivityDtoMapper<T extends TaskActivityElement, D extends TaskActivityElementDto> {

    private final UserRepresentationMapper userRepresentationMapper;

    public final TaskActivityElementDto toDto(TaskActivityElement element, UserRepresentation user) {
        return toTypedDto(cast(element), user);
    }

    protected void mapBase(TaskActivityElement element,
                           TaskActivityElementDto.TaskActivityElementDtoBuilder<?, ?> b,
                           UserRepresentation user) {
        MongoUserDto mongoUserDto = userRepresentationMapper.toDto(user);
        b
                .user(mongoUserDto)
                .createdAt(element.getCreatedAt());
    }

    @SuppressWarnings("unchecked")
    public T cast(TaskActivityElement element) {
        return (T) element;
    }

    protected abstract D toTypedDto(T element, UserRepresentation user);

    public abstract Class<T> getSupportedType();
}
