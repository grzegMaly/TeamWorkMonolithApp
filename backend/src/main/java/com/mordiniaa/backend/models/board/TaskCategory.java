package com.mordiniaa.backend.models.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TaskCategory {

    private int position = 0;

    private String categoryName;

    private List<ObjectId> tasks;

    private Instant createdAt;
}
