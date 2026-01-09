package com.mordiniaa.backend.request.note.deadline;

import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.request.note.CreateNoteRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateDeadlineNoteRequest extends CreateNoteRequest implements DeadlineNoteRequest {

    @NotNull(message = "Status is required")
    private Status status;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private Instant deadline;
}
