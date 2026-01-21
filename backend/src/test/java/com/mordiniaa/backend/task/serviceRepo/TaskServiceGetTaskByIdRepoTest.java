package com.mordiniaa.backend.task.serviceRepo;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.mappers.User.UserRepresentationMapper;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.TaskActivityMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskCategoryChangeDtoMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskCommentDtoMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskStatusChangeDtoMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.request.task.CreateTaskRequest;
import com.mordiniaa.backend.services.notes.task.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@DataMongoTest
@ActiveProfiles("test")
@Import({
        TaskMapper.class,
        TaskService.class,
        TaskActivityMapper.class,
        TaskCommentDtoMapper.class,
        TaskCategoryChangeDtoMapper.class,
        TaskStatusChangeDtoMapper.class,
        UserRepresentationMapper.class,
        BoardAggregationRepositoryImpl.class
})
public class TaskServiceGetTaskByIdRepoTest {

    @MockitoBean("mongoAuditor")
    private AuditorAware<String> auditorAware;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepresentationRepository userRepresentationRepository;

    @Autowired
    private TaskService taskService;

    private final Set<BoardPermission> boardPermissions = Set.of(BoardPermission.VIEW_BOARD);
    private final Set<TaskPermission> taskPermissions = Set.of(TaskPermission.CREATE_TASK, TaskPermission.ASSIGN_TASK);

    private final UUID ownerId = UUID.randomUUID();
    private final UUID member1Id = UUID.randomUUID();
    private final UUID member2Id = UUID.randomUUID();

    private final String boardCategoryName = "Testing";

    private Board board;

    private final String title = "Title";
    private final String description = "Description";
    private final Instant deadline = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);

    private UserRepresentation boardOwner;
    private UserRepresentation user1;
    private UserRepresentation user2;

    private BoardMember boardMemberOwner;
    private BoardMember boardMember1;
    private BoardMember boardMember2;

    private String taskId;

    @BeforeEach
    void setUp() {

        boardOwner = new UserRepresentation();
        boardOwner.setUserId(ownerId);
        boardOwner.setUsername("Owner 1");
        boardOwner.setImageUrl("https://random1.pl");

        user1 = new UserRepresentation();
        user1.setUserId(member1Id);
        user1.setUsername("XXXX");
        user1.setImageUrl("https://random1.pl");

        user2 = new UserRepresentation();
        user2.setUserId(member2Id);
        user2.setUsername("XXXX");
        user2.setImageUrl("https://random2.pl");
        userRepresentationRepository.saveAll(List.of(boardOwner, user1, user2));

        boardMemberOwner = new BoardMember();
        boardMemberOwner.setUserId(ownerId);
        boardMemberOwner.setBoardPermissions(boardPermissions);
        boardMemberOwner.setTaskPermissions(taskPermissions);

        boardMember1 = new BoardMember();
        boardMember1.setUserId(member1Id);
        boardMember1.setBoardPermissions(boardPermissions);

        boardMember2 = new BoardMember();
        boardMember2.setUserId(member2Id);
        boardMember2.setBoardPermissions(boardPermissions);

        TaskCategory boardCategory = new TaskCategory();
        boardCategory.setCategoryName(boardCategoryName);
        board = new Board();
        board.setBoardName("Test 1");
        board.setOwner(this.boardMemberOwner);
        board.setMembers(List.of(boardMember1, boardMember2));
        board.setTaskCategories(List.of(boardCategory));

        board = boardRepository.save(board);

        CreateTaskRequest createTaskRequest = new CreateTaskRequest(title, description, deadline);
        createTaskRequest.setAssignedTo(Set.of(ownerId, member1Id, member2Id));
        taskId = taskService.createTask(ownerId, board.getId().toHexString(), boardCategoryName, createTaskRequest).getId();
    }

    @AfterEach
    void clearEnv() {
        userRepresentationRepository.deleteAll();
        boardRepository.deleteAll();
        taskRepository.deleteAll();
    }
}
