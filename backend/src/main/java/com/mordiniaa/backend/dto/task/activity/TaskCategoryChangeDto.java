package com.mordiniaa.backend.dto.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TaskCategoryChangeDto extends TaskActivityElementDto {

    private String prevTaskCategoryName;
    private String nextTaskCategoryName;
}
