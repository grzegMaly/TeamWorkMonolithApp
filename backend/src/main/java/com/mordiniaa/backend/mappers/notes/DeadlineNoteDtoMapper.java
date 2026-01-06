package com.mordiniaa.backend.mappers.notes;

import com.mordiniaa.backend.dto.DeadlineNoteDto;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import org.springframework.stereotype.Component;

@Component
public class DeadlineNoteDtoMapper extends AbstractNoteDtoMapper<DeadlineNote, DeadlineNoteDto> {

    @Override
    public boolean supportsTyped(Note note) {
        return note instanceof DeadlineNote;
    }

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
}
