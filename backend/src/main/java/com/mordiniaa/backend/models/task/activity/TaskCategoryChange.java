package com.mordiniaa.backend.models.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;

import java.util.UUID;

@Getter
@Setter
@TypeAlias("category_change")
@NoArgsConstructor
public class TaskCategoryChange extends TaskActivityElement {

    private String prevCategory;
    private String nextCategory;

    public TaskCategoryChange(UUID user) {
        super(user);
    }
}
