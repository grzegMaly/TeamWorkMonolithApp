package com.mordiniaa.backend.dto.board;

import com.mordiniaa.backend.dto.task.TaskCardDto;
import com.mordiniaa.backend.dto.user.mongodb.UserProjection;

import java.time.Instant;
import java.util.List;

public class BoardDetailsDTO {

    private String boardId;
    private String boardName;
    private List<TaskCategoryDTO> taskCategories;
    private List<UserProjection> members;
    private Instant createdAt;

    private static class TaskCategoryDTO {

        private int position;
        private String categoryName;
        private List<TaskCardDto> tasks;
        private Instant createdAt;
    }
}
