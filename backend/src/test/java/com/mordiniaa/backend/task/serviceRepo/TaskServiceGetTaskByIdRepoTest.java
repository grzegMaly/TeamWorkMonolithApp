package com.mordiniaa.backend.task.serviceRepo;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.activity.TaskActivityElementDto;
import com.mordiniaa.backend.dto.task.activity.TaskCommentDto;
import com.mordiniaa.backend.dto.task.activity.TaskStatusChangeDto;
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
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.TaskStatus;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import com.mordiniaa.backend.models.task.activity.TaskCategoryChange;
import com.mordiniaa.backend.models.task.activity.TaskComment;
import com.mordiniaa.backend.models.task.activity.TaskStatusChange;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.request.task.CreateTaskRequest;
import com.mordiniaa.backend.services.notes.task.TaskService;
import org.bson.types.ObjectId;
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

import static org.junit.jupiter.api.Assertions.*;


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

    private final Instant createdAt = Instant.now().minus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate1 = createdAt.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate2 = activityDate1.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate3 = activityDate2.plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate4 = activityDate3.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate5 = activityDate4.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate6 = activityDate5.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);

    private final TaskComment taskComment1 = getTaskComment(ownerId, "Comment 1", activityDate1);
    private final TaskCategoryChange taskCategoryChange1 = getTaskCategoryChange(member1Id, "Started", "In Progres", activityDate2);
    private final TaskComment taskComment2 = getTaskComment(member1Id, "Comment 2", activityDate3);
    private final TaskStatusChange taskStatusChange1 = getTaskStatusChange(member2Id, TaskStatus.UNCOMPLETED, TaskStatus.COMPLETED, activityDate4);
    private final TaskCategoryChange taskCategoryChange2 = getTaskCategoryChange(member1Id, "In Progres", "Done", activityDate5);
    private final TaskStatusChange taskStatusChange2 = getTaskStatusChange(member2Id, TaskStatus.COMPLETED, TaskStatus.UNCOMPLETED, activityDate6);

    @BeforeEach
    void setUp() {

        boardOwner = new UserRepresentation();
        boardOwner.setUserId(ownerId);
        boardOwner.setUsername("Owner 1");
        boardOwner.setImageUrl("https://random1.pl");

        user1 = new UserRepresentation();
        user1.setUserId(member1Id);
        user1.setUsername("Mem 1");
        user1.setImageUrl("https://random1.pl");

        user2 = new UserRepresentation();
        user2.setUserId(member2Id);
        user2.setUsername("Mem 2");
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

        TaskCategory boardCategory = new TaskCategory();
        String boardCategoryName = "Testing";
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

        Task task = taskRepository.findById(new ObjectId(taskId)).orElseThrow(RuntimeException::new);
        task.setActivityElements(List.of(taskComment1, taskCategoryChange1, taskComment2, taskStatusChange1, taskCategoryChange2, taskStatusChange2));
        taskRepository.save(task);
    }

    @AfterEach
    void clearEnv() {
        userRepresentationRepository.deleteAll();
        boardRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Get Task By Id And Owner Id Valid Test")
    void getTaskByIdOwnerIdValidTest() {

        TaskDetailsDTO taskDetailsDTO = taskService.getTaskDetailsById(ownerId, board.getId().toHexString(), taskId);
        assertNotNull(taskDetailsDTO);
        assertEquals(title, taskDetailsDTO.getTitle());
        assertEquals(description, taskDetailsDTO.getDescription());
        assertEquals(TaskStatus.UNCOMPLETED, taskDetailsDTO.getTaskStatus());

        assertNotNull(taskDetailsDTO.getTaskStatus());
        assertFalse(taskDetailsDTO.getTaskActivityElements().isEmpty());
        assertEquals(6, taskDetailsDTO.getTaskActivityElements().size());

        TaskActivityElementDto latestElement = taskDetailsDTO.getTaskActivityElements().getFirst();
        assertNotNull(latestElement);
        assertEquals(activityDate6, latestElement.getCreatedAt());
        assertEquals(member2Id, latestElement.getUser().getUserId());
        assertEquals("Mem 2", latestElement.getUser().getUsername());

        assertInstanceOf(TaskStatusChangeDto.class, latestElement);
        TaskStatusChangeDto latestCastedElement = (TaskStatusChangeDto) latestElement;
        assertEquals(taskStatusChange2.getPrevStatus(), latestCastedElement.getPrevStatus());
        assertEquals(taskStatusChange2.getNextStatus(), latestCastedElement.getNextStatus());

        TaskActivityElementDto oldestElement = taskDetailsDTO.getTaskActivityElements().getLast();
        assertNotNull(oldestElement);
        assertEquals(activityDate1, oldestElement.getCreatedAt());
        assertEquals(ownerId, oldestElement.getUser().getUserId());
        assertEquals("Owner 1", oldestElement.getUser().getUsername());

        assertInstanceOf(TaskCommentDto.class, oldestElement);
        TaskCommentDto oldestCastedElement = (TaskCommentDto) oldestElement;
        assertEquals(taskComment1.getComment(), oldestCastedElement.getComment());
        assertEquals(taskComment1.isUpdated(), oldestCastedElement.isUpdated());
    }

    @Test
    @DisplayName("Get Task By Id And Member Id Valid Test")
    void getTaskByIdAndMemberIdValidTest() {

        TaskDetailsDTO taskDetailsDTO = taskService.getTaskDetailsById(member1Id, board.getId().toHexString(), taskId);
        assertNotNull(taskDetailsDTO);
        assertEquals(title, taskDetailsDTO.getTitle());
        assertEquals(description, taskDetailsDTO.getDescription());
        assertEquals(TaskStatus.UNCOMPLETED, taskDetailsDTO.getTaskStatus());

        assertNotNull(taskDetailsDTO.getTaskStatus());
        assertFalse(taskDetailsDTO.getTaskActivityElements().isEmpty());
        assertEquals(6, taskDetailsDTO.getTaskActivityElements().size());
    }

    @Test
    @DisplayName("Get Task By Id And Member Without Permission Invalid Test")
    void getTaskByIdAndMemberWithoutPermissionInvalidTest() {

        assertThrows(RuntimeException.class,
                () -> taskService.getTaskDetailsById(member2Id, board.getId().toHexString(), taskId));
    }

    @Test
    @DisplayName("Get Task By Id User Not Found Test")
    void getTaskByIdUserNotFoundTest() {

        assertThrows(RuntimeException.class,
                () -> taskService.getTaskDetailsById(UUID.randomUUID(), board.getId().toHexString(), taskId));
    }

    private TaskComment getTaskComment(UUID userId, String comment, Instant time) {
        TaskComment taskComment = new TaskComment();
        taskComment.setCreatedAt(time);
        taskComment.setComment(comment);
        taskComment.setUser(userId);
        return taskComment;
    }

    private TaskCategoryChange getTaskCategoryChange(UUID userId, String prevCategoryName, String nextCategoryName, Instant time) {
        TaskCategoryChange categoryChange = new TaskCategoryChange();
        categoryChange.setUser(userId);
        categoryChange.setPrevCategory(prevCategoryName);
        categoryChange.setNextCategory(nextCategoryName);
        categoryChange.setCreatedAt(time);
        return categoryChange;
    }

    private TaskStatusChange getTaskStatusChange(UUID userId, TaskStatus prevStatus, TaskStatus nextStatus, Instant time) {
        TaskStatusChange statusChange = new TaskStatusChange();
        statusChange.setUser(userId);
        statusChange.setPrevStatus(prevStatus);
        statusChange.setNextStatus(nextStatus);
        statusChange.setCreatedAt(time);
        return statusChange;
    }
}
