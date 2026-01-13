package com.mordiniaa.backend.mappers.note.dtoMappers;

import com.mordiniaa.backend.dto.note.DeadlineNoteDto;
import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import org.springframework.stereotype.Component;

@Component
public class DeadlineNoteDtoMapper extends AbstractNoteDtoMapper<DeadlineNote, DeadlineNoteDto> {

    @Override
    public DeadlineNoteDto toDtoTyped(DeadlineNote note) {
        DeadlineNoteDto.DeadlineNoteDtoBuilder<?, ?> builder =
                DeadlineNoteDto.builder();

        mapBase(note, builder);

        return builder
                .priority(note.getPriority())
                .status(note.getStatus())
                .deadline(note.getDeadline())
                .build();
    }

    @Override
    public Class<DeadlineNote> getSupportedClass() {
        return DeadlineNote.class;
    }
}
