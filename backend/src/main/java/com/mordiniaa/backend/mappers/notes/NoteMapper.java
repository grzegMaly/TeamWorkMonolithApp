package com.mordiniaa.backend.mappers.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.mappers.notes.dtoMappers.AbstractNoteDtoMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.AbstractNoteModelMapper;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.request.note.NoteRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NoteMapper {

    private final Map<Class<? extends Note>, AbstractNoteDtoMapper<?, ?>> mapperByModelType;
    private final Map<Class<? extends NoteRequest>, AbstractNoteModelMapper<?, ?>> mapperByDtoType;

    public NoteMapper(List<AbstractNoteDtoMapper<?, ?>> dtoMappers, List<AbstractNoteModelMapper<?, ?>> modelMappers) {
        this.mapperByModelType = dtoMappers.stream()
                .collect(Collectors.toMap(
                        AbstractNoteDtoMapper::getSupportedClass,
                        Function.identity()
                ));

        this.mapperByDtoType = modelMappers.stream()
                .flatMap(mapper ->
                        mapper.getSupportedClasses().stream()
                                .map(clazz -> Map.entry(clazz, mapper))
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public NoteDto toDto(Note note) {

        AbstractNoteDtoMapper<?, ?> mapper = mapperByModelType.get(note.getClass());

        if (mapper == null) {
            throw new RuntimeException();
        }
        return mapper.toDto(note);
    }

    public Note toModel(NoteRequest noteRequest) {

        AbstractNoteModelMapper<?, ?> mapper = mapperByDtoType.get(noteRequest.getClass());
        if (mapper == null) {
            throw new RuntimeException();
        }
        return mapper.toModel(noteRequest);
    }
}
