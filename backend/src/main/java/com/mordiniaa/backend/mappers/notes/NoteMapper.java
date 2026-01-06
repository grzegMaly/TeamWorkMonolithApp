package com.mordiniaa.backend.mappers.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.models.notes.Note;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NoteMapper {

    private final Map<Class<? extends Note>, AbstractNoteDtoMapper<?, ?>> mapperByType;

    public NoteMapper(List<AbstractNoteDtoMapper<?, ?>> mappers) {
        this.mapperByType = mappers.stream()
                .collect(Collectors.toMap(
                        AbstractNoteDtoMapper::getSupportedClass,
                        Function.identity()
                ));
    }

    public NoteDto toDto(Note note) {

        AbstractNoteDtoMapper<?, ?> mapper = mapperByType.get(note.getClass());

        if (mapper == null) {
            throw new RuntimeException();
        }
        return mapper.toDto(note);
    }
}
