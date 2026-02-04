package com.mordiniaa.backend.models.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class TaskCategory {

    private int position = 0;

    private String categoryName;

    private Set<ObjectId> tasks = new HashSet<>();

    private Instant createdAt;

    public void addTaskId(ObjectId taskId) {
        tasks.add(taskId);
    }

    public void deleteTaskId(ObjectId taskId) {
        tasks.remove(taskId);
    }

    public void lowerPosition() {
        position--;
    }

    public void higherPosition() {
        position++;
    }
}
