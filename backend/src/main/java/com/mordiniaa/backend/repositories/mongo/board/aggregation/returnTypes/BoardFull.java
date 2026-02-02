package com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes;

import com.mordiniaa.backend.mappers.user.UserRepresentationMapper;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.BoardMembers;
import com.mordiniaa.backend.models.board.BoardTemplate;
import com.mordiniaa.backend.models.task.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BoardAndTasks implements BoardMembers, BoardTemplate {

    private ObjectId id;
    private UserRepresentationMapper owner;
    private List<UserRepresentationMapper> members;
    private UUID teamId;
    private String boardName;
    private List<TaskCategoryFull> taskCategories;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TaskCategoryFull{

        private int position = 0;

        private String categoryName;

        private Set<Task> tasks = new HashSet<>();

        private Instant createdAt;
    }
}
