package com.mordiniaa.backend.mappers.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.models.notes.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoteMapper {

    private final List<AbstractNoteDtoMapper<?,?>> mappers;

    public NoteDto toDto(Note note) {
        return mappers.stream()
                .filter(m -> m.supportsTyped(note))
                .findFirst()
                .orElseThrow()
                .toDto(note);
    }
}
