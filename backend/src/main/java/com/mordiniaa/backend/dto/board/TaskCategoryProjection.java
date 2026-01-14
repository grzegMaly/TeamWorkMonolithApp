package com.mordiniaa.backend.dto.board;

import com.mordiniaa.backend.dto.board.task.TaskProjection;

import java.util.List;

public interface TaskCategoryProjection {

    int getPosition();

    String getCategoryName();

    List<TaskProjection> getTasks();
}
