package com.mordiniaa.backend.mappers.note.dtoMappers;

import com.mordiniaa.backend.dto.note.NoteDto;
import com.mordiniaa.backend.models.note.Note;

public abstract class AbstractNoteDtoMapper<T extends Note, D extends NoteDto> {

    public final NoteDto toDto(Note note) {
        return toDtoTyped(cast(note));
    }

    protected void mapBase(Note note, NoteDto.NoteDtoBuilder<?, ?> builder) {
        builder
                .id(note.getId().toHexString())
                .title(note.getTitle())
                .content(note.getContent())
                .ownerId(note.getOwnerId())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt());
    }

    @SuppressWarnings("unchecked")
    private T cast(Note note) {
        return (T) note;
    }

    protected abstract D toDtoTyped(T note);

    public abstract Class<T> getSupportedClass();
}
