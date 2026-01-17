package com.mordiniaa.backend.mappers.note.modelMappers;

import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import com.mordiniaa.backend.request.note.deadline.CreateDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.deadline.DeadlineNoteRequest;
import com.mordiniaa.backend.request.note.NoteRequest;
import com.mordiniaa.backend.request.note.deadline.PatchDeadlineNoteRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
    public Set<Class<? extends NoteRequest>> getSupportedRequestClasses() {
        return Set.of(
                CreateDeadlineNoteRequest.class,
                PatchDeadlineNoteRequest.class
        );
    }

    @Override
    public Class<DeadlineNote> getSupportedClass() {
        return DeadlineNote.class;
    }

    @Override
    protected void updateModelTyped(DeadlineNote note, DeadlineNoteRequest noteRequest) {

        super.updateModelTyped(note, noteRequest);

        if (noteRequest.getDeadline() != null && noteRequest.getDeadline().isAfter(Instant.now()))
            note.setDeadline(noteRequest.getDeadline());

        if (noteRequest.getPriority() != null)
            note.setPriority(noteRequest.getPriority());

        if (noteRequest.getStatus() != null)
            note.setStatus(noteRequest.getStatus());
    }
}
