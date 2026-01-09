package com.mordiniaa.backend.request.note.deadline;

import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.request.note.PatchNoteRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class PatchDeadlineNoteRequest extends PatchNoteRequest implements DeadlineNoteRequest {

    private Status status;
    private Priority priority;
    private Instant deadline;
}
