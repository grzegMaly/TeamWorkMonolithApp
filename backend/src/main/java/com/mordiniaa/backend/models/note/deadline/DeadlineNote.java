package com.mordiniaa.backend.models.note.deadline;

import com.mordiniaa.backend.models.note.Note;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("deadline")
@ToString(callSuper = true)
@Document(collection = "notes")
public class DeadlineNote extends Note {

    @Field(name = "priority", order = 100)
    private Priority priority;

    @Builder.Default
    @Field(name = "status", order = 101)
    private Status status = Status.NEW;

    @Field(name = "deadline", order = 103)
    private Instant deadline;

    public void setDeadline(Instant deadline) {
        this.deadline = deadline == null
                ? null
                : deadline.truncatedTo(ChronoUnit.MILLIS);
    }
}
