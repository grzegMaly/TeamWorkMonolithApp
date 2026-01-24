package com.mordiniaa.backend.request.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
public class PatchTaskDataRequest {

    private String title;
    private String description;
    private Instant deadline;

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline.truncatedTo(ChronoUnit.MILLIS);
    }
}
