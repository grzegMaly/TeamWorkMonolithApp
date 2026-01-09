package com.mordiniaa.backend.mappers.notes.modelMappers;

import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.request.note.deadline.CreateDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.deadline.DeadlineNoteRequest;
import com.mordiniaa.backend.request.note.NoteRequest;
import com.mordiniaa.backend.request.note.deadline.PatchDeadlineNoteRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DeadlineNoteModelMapper extends AbstractNoteModelMapper<DeadlineNoteRequest, DeadlineNote> {

    @Override
    protected DeadlineNote toModelTyped(DeadlineNoteRequest noteRequest) {

        DeadlineNote.DeadlineNoteBuilder<?, ?> builder = DeadlineNote.builder();
        mapBase(noteRequest, builder);
        return builder
                .status(noteRequest.getStatus())
                .priority(noteRequest.getPriority())
                .deadline(noteRequest.getDeadline())
                .build();
    }

    @Override
    public Set<Class<? extends NoteRequest>> getSupportedClasses() {
        return Set.of(
                CreateDeadlineNoteRequest.class,
                PatchDeadlineNoteRequest.class
        );
    }
}
