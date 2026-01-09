package com.mordiniaa.backend.request.note;

import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class CreateDeadlineNoteRequest extends CreateNoteRequest implements DeadlineNoteRequest {

    private Status status;
    private Priority priority;
    private Instant deadline;
}
