package com.mordiniaa.backend.dto;

import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
public class DeadlineNoteDto extends NoteDto {

    private Priority priority;
    private Status status;
    private Instant deadline;
}
