package com.mordiniaa.backend.dto.note;

import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DeadlineNoteDto extends NoteDto {

    private Priority priority;
    private Status status;
    private Instant deadline;
}
