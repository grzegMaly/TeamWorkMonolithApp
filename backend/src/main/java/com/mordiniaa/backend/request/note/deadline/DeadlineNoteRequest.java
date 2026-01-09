package com.mordiniaa.backend.request.note;

import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;

import java.time.Instant;

public interface DeadlineNoteRequest extends NoteRequest {

    Status getStatus();

    Priority getPriority();

    Instant getDeadline();
}
