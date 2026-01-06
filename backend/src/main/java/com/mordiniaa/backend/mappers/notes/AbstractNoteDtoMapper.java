package com.mordiniaa.backend.mappers.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.models.notes.Note;

public abstract class AbstractNoteDtoMapper<T extends Note, D extends NoteDto> {

    public final NoteDto toDto(Note note) {
        return toDtoTyped(cast(note));
    }

    protected void mapBase(Note note, NoteDto.NoteDtoBuilder<?, ?> builder) {
        builder
                .id(note.getId().toHexString())
                .title(note.getTitle())
                .content(note.getContent())
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
