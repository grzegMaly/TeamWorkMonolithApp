package com.mordiniaa.backend.dto.task.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskCategoryChangeDto extends TaskActivityElementDto {

    private String prevTaskCategoryName;
    private String nextTaskCategoryName;
}
