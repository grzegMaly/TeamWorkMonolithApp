package com.mordiniaa.backend.mappers.note.dtoMappers;

import com.mordiniaa.backend.dto.note.RegularNoteDto;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import org.springframework.stereotype.Component;

@Component
public class RegularNoteDtoMapper extends AbstractNoteDtoMapper<RegularNote, RegularNoteDto> {

    @Override
    public RegularNoteDto toDtoTyped(RegularNote note) {
        RegularNoteDto.RegularNoteDtoBuilder<?, ?> builder = RegularNoteDto.builder();
        mapBase(note, builder);
        return builder
                .category(note.getCategory())
                .build();
    }

    @Override
    public Class<RegularNote> getSupportedClass() {
        return RegularNote.class;
    }
}
