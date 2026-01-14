package com.mordiniaa.backend.dto.board;

import com.mordiniaa.backend.dto.user.mongodb.UserProjection;
import com.mordiniaa.backend.models.board.task.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class BoardDetailsDTO {

    private String boardId;
    private String boardName;
    private List<TaskCategoryDTO> taskCategories;
    private List<UserProjection> members;
    private Instant createdAt;

    private static class TaskCategoryDTO {

        private int position;
        private String categoryName;
        private List<TaskCardDTO> tasks;
        private Instant createdAt;

        private static class TaskCardDTO {

            private String id;
            private int positionInCategory;
            private String title;
            private String description;
            private TaskStatus taskStatus;
            private List<UUID> assignedTo;
            private Instant deadline;
        }
    }
}
