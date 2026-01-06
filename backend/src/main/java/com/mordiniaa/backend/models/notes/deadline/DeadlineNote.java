package com.mordiniaa.backend.models.notes.deadline;

import com.mordiniaa.backend.models.notes.Note;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@TypeAlias("deadline")
@Document(collection = "notes")
public class DeadlineNote extends Note {

    @Field(name = "priority", order = 100)
    private Priority priority;

    @Field(name = "status", order = 101)
    private Status status = Status.NEW;

    @Field(name = "deadline", order = 103)
    private Instant deadline;
}
