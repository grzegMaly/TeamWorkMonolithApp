package com.mordiniaa.backend.models.board.tasks;

import com.mordiniaa.backend.models.board.tasks.activity.TaskActivityElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document("tasks")
public class Task {

    @Id
    private ObjectId id;

    private String title;
    private String description;

    private TaskStatus taskStatus;
    private List<TaskActivityElement> activityElements;

    private UUID createdBy;
    private List<UUID> assignedTo;

    private Instant createdAt;
    private Instant updatedAt;

    private Instant deadline;
}
