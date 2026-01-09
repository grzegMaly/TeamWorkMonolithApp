package com.mordiniaa.backend.mappers.notes.modelMappers;

import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.request.note.regular.CreateRegularNoteRequest;
import com.mordiniaa.backend.request.note.NoteRequest;
import com.mordiniaa.backend.request.note.regular.PatchRegularNoteRequest;
import com.mordiniaa.backend.request.note.regular.RegularNoteRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RegularNoteModelMapper extends AbstractNoteModelMapper<RegularNoteRequest, RegularNote> {

    @Override
    protected RegularNote toModelTyped(RegularNoteRequest regularNoteRequest) {
        RegularNote.RegularNoteBuilder<?, ?> builder = RegularNote.builder();
        mapBase(regularNoteRequest, builder);
        return builder.category(regularNoteRequest.getCategory())
                .build();
    }

    @Override
    public Set<Class<? extends NoteRequest>> getSupportedClasses() {
        return Set.of(
                CreateRegularNoteRequest.class,
                PatchRegularNoteRequest.class
        );
    }
}
