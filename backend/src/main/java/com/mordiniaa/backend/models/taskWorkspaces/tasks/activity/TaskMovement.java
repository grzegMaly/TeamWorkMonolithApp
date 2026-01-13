package com.mordiniaa.backend.models.taskWorkspaces.tasks.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;


@Getter
@Setter
@TypeAlias("movement")
@NoArgsConstructor
public class TaskMovement extends TaskActivityElement {

    private String prevCategory;
    private String newCategory;
}
