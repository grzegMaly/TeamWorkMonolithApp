package com.mordiniaa.backend.models.task;

import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document("tasks")
@TypeAlias("task")
public class Task {

    @Id
    private ObjectId id;

    private int positionInCategory;

    private String title;
    private String description;

    private TaskStatus taskStatus = TaskStatus.UNCOMPLETED;
    private List<TaskActivityElement> activityElements = new ArrayList<>();

    private UUID createdBy;
    private Set<UUID> assignedTo = new HashSet<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant deadline;

    public void addMember(UUID boardMember) {
        if (assignedTo == null)
            assignedTo = new HashSet<>();
        this.assignedTo.add(boardMember);
    }

    public void addMembers(Collection<UUID> boardMembers) {
        if (assignedTo == null)
            assignedTo = new HashSet<>();
        this.assignedTo.addAll(boardMembers);
    }
}
