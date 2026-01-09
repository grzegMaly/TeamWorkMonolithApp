package com.mordiniaa.backend.mappers.notes.modelMappers;

import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.request.note.NoteRequest;

import java.util.Set;

public abstract class AbstractNoteModelMapper<T extends NoteRequest, D extends Note> {

    public final Note toModel(NoteRequest noteRequest) {
        return toModelTyped(cast(noteRequest));
    }

    protected void mapBase(NoteRequest noteRequest, Note.NoteBuilder<?, ?> builder) {
        builder.archived(false)
                .title(noteRequest.getTitle())
                .content(noteRequest.getContent());
    }

    @SuppressWarnings("unchecked")
    private T cast(NoteRequest noteRequest) {
        return (T) noteRequest;
    }

    protected abstract D toModelTyped(T noteRequest);

    public abstract Set<Class<? extends NoteRequest>> getSupportedClasses();
}
