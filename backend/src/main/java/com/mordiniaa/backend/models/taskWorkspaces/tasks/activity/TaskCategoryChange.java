package com.mordiniaa.backend.models.taskWorkspaces.tasks.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("category_change")
@NoArgsConstructor
public class TaskCategoryChange extends TaskActivityElement {

    private ObjectId prevCategory;
    private ObjectId nextCategory;
}
