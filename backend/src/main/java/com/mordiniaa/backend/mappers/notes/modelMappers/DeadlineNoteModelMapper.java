package com.mordiniaa.backend.mappers.notes.modelMappers;

import com.mordiniaa.backend.dto.DeadlineNoteDto;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import org.springframework.stereotype.Component;

@Component
public class DeadlineNoteDtoMapper extends AbstractNoteModelMapper<DeadlineNoteDto, DeadlineNote> {
    @Override
    protected DeadlineNote toModelTyped(DeadlineNoteDto noteDto) {

        DeadlineNote.DeadlineNoteBuilder<?, ?> builder = DeadlineNote.builder();
        mapBase(noteDto, builder);
        return builder
                .status(noteDto.getStatus())
                .priority(noteDto.getPriority())
                .deadline(noteDto.getDeadline())
                .build();
    }

    @Override
    public Class<DeadlineNoteDto> getSupportedClass() {
        return DeadlineNoteDto.class;
    }
}
