package com.mordiniaa.backend.mappers.notes.modelMappers;

import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.models.notes.regular.RegularNote;

public class RegularNoteModelMapper extends AbstractNoteModelMapper<RegularNoteDto, RegularNote> {
    @Override
    protected RegularNote toModelTyped(RegularNoteDto noteDto) {
        RegularNote.RegularNoteBuilder<?, ?> builder = RegularNote.builder();
        mapBase(noteDto, builder);
        return builder.category(noteDto.getCategory())
                .build();
    }

    @Override
    public Class<RegularNoteDto> getSupportedClass() {
        return RegularNoteDto.class;
    }
}
