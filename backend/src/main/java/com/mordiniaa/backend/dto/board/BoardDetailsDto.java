package com.mordiniaa.backend.dto.board;

import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.dto.user.mongodb.UserDto;

import java.time.Instant;
import java.util.List;

public class BoardDetailsDto {

    private String boardId;
    private String boardName;
    private List<TaskCategoryDTO> taskCategories;
    private List<UserDto> members;
    private Instant createdAt;

    private static class TaskCategoryDTO {

        private int position;
        private String categoryName;
        private List<TaskShortDto> tasks;
        private Instant createdAt;
    }
}
