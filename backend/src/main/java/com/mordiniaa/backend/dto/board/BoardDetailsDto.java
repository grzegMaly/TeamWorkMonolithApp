package com.mordiniaa.backend.dto.board;

import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.dto.user.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardDetailsDto extends BoardShortDto {

    private UserDto owner;
    private List<TaskCategoryDTO> taskCategories;
    private List<UserDto> members;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TaskCategoryDTO {

        private int position;
        private String categoryName;
        private List<TaskShortDto> tasks;
        private Instant createdAt;
    }
}
