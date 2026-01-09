package com.mordiniaa.backend.mappers.notes.modelMappers;

import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.request.note.NoteRequest;

import java.util.Set;

public abstract class AbstractNoteModelMapper<T extends NoteRequest, D extends Note> {

    protected abstract D toModelTyped(T noteRequest);

    public abstract Set<Class<? extends NoteRequest>> getSupportedRequestClasses();

    public abstract Class<? extends Note> getSupportedClass();

    @SuppressWarnings("unchecked")
    private T cast(NoteRequest noteRequest) {
        return (T) noteRequest;
    }

    @SuppressWarnings("unchecked")
    private D cast(Note note) {
        return (D) note;
    }

    public final Note toModel(NoteRequest noteRequest) {
        return toModelTyped(cast(noteRequest));
    }

    public final void updateNote(Note note, NoteRequest noteRequest) {
        updateModelTyped(cast(note), cast(noteRequest));
    }

    protected void mapBase(NoteRequest noteRequest, Note.NoteBuilder<?, ?> builder) {
        builder.archived(false)
                .title(noteRequest.getTitle().trim())
                .content(noteRequest.getContent().trim());
    }

    protected void updateBase(Note note, NoteRequest noteRequest) {
        if (checkUpdateString(noteRequest.getTitle()))
            note.setTitle(noteRequest.getTitle().trim());

        if (checkUpdateString(noteRequest.getContent()))
            note.setContent(noteRequest.getContent().trim());
    }

    protected void updateModelTyped(D note, T noteRequest) {
        updateBase(note, noteRequest);
    }

    protected boolean checkUpdateString(String field) {
        return field != null && !field.isBlank();
    }
}
