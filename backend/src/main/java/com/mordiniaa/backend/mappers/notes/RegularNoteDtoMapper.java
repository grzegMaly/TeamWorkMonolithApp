package com.mordiniaa.backend.mappers.notes;

import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import org.springframework.stereotype.Component;

@Component
public class RegularNoteDtoMapper extends AbstractNoteDtoMapper<RegularNote, RegularNoteDto> {

    @Override
    public boolean supportsTyped(Note note) {
        return note instanceof RegularNote;
    }

    @Override
    public RegularNoteDto toDtoTyped(RegularNote note) {
        RegularNoteDto.RegularNoteDtoBuilder<?, ?> builder = RegularNoteDto.builder();
        mapBase(note, builder);
        return builder
                .category(note.getCategory())
                .build();
    }
}
