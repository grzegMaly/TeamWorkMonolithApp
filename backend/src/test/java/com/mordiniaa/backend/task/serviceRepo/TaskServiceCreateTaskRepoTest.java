package com.mordiniaa.backend.task.serviceRepo;

import com.mordiniaa.backend.dto.task.TaskShortDto;
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
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataMongoTest
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
public class TaskServiceCreateTaskRepoTest {

    @MockitoBean("mongoAuditor")
    private AuditorAware<String> auditorAware;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepresentationRepository userRepresentationRepository;

    @Autowired
    private TaskService taskService;

    private final Set<BoardPermission> boardPermissions = Set.of(BoardPermission.VIEW_BOARD);
    private final Set<TaskPermission> taskPermissions1 = Set.of(TaskPermission.CREATE_TASK, TaskPermission.ASSIGN_TASK);
    private final Set<TaskPermission> taskPermissions2 = Set.of(TaskPermission.CREATE_TASK);

    private final UUID owner1Id = UUID.randomUUID();
    private final UUID owner2Id = UUID.randomUUID();
    private final UUID owner3Id = UUID.randomUUID();

    private final UUID member11Id = UUID.randomUUID();
    private final UUID member12Id = UUID.randomUUID();
    private final UUID member21Id = UUID.randomUUID();
    private final UUID member22Id = UUID.randomUUID();

    private final String board1CategoryName = "Testing";

    private Board board1;

    private final String title = "Title";
    private final String description = "Description";
    private final Instant deadline = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);

    private CreateTaskRequest createTaskRequest;


    @BeforeEach
    void setEnv() {

        createTaskRequest = new CreateTaskRequest(title, description, deadline);

        UserRepresentation boardOwner1 = new UserRepresentation();
        boardOwner1.setUserId(owner1Id);
        boardOwner1.setUsername("Owner 1");
        boardOwner1.setImageUrl("https://random1.pl");

        UserRepresentation boardOwner2 = new UserRepresentation();
        boardOwner2.setUserId(owner2Id);
        boardOwner2.setUsername("Owner 2");
        boardOwner2.setImageUrl("https://random2.pl");

        UserRepresentation deletedBoardOwner3 = new UserRepresentation();
        deletedBoardOwner3.setUserId(owner3Id);
        deletedBoardOwner3.setUsername("Owner 3");
        deletedBoardOwner3.setImageUrl("https://random3.pl");
        deletedBoardOwner3.setDeleted(true);

        UserRepresentation user11 = new UserRepresentation();
        user11.setUserId(member11Id);
        user11.setUsername("XXXX");
        user11.setImageUrl("https://random1.pl");

        UserRepresentation user12 = new UserRepresentation();
        user12.setUserId(member12Id);
        user12.setUsername("XXXX");
        user12.setImageUrl("https://random2.pl");

        userRepresentationRepository.saveAll(List.of(boardOwner1, boardOwner2, deletedBoardOwner3, user11, user12));

        BoardMember boardMemberOwner1 = new BoardMember();
        boardMemberOwner1.setUserId(owner1Id);
        boardMemberOwner1.setBoardPermissions(boardPermissions);
        boardMemberOwner1.setTaskPermissions(taskPermissions1);

        BoardMember boardMemberOwner2 = new BoardMember();
        boardMemberOwner2.setUserId(owner2Id);
        boardMemberOwner2.setBoardPermissions(boardPermissions);
        boardMemberOwner2.setTaskPermissions(taskPermissions1);

        BoardMember member11 = new BoardMember();
        member11.setUserId(member11Id);
        member11.setBoardPermissions(boardPermissions);
        member11.setTaskPermissions(taskPermissions1);

        BoardMember member12 = new BoardMember();
        member12.setUserId(member12Id);
        member12.setBoardPermissions(boardPermissions);
        member12.setTaskPermissions(taskPermissions2);

        BoardMember member21 = new BoardMember();
        member21.setUserId(member21Id);

        BoardMember member22 = new BoardMember();
        member22.setUserId(member22Id);

        TaskCategory board1Category = new TaskCategory();
        board1Category.setCategoryName(board1CategoryName);
        board1 = new Board();
        board1.setBoardName("Test 1");
        board1.setOwner(boardMemberOwner1);
        board1.setMembers(List.of(member11, member12));
        board1.setTaskCategories(List.of(board1Category));

        TaskCategory board2Category = new TaskCategory();
        String board2CategoryName = "Developing";
        board2Category.setCategoryName(board2CategoryName);
        Board board2 = new Board();
        board2.setBoardName("Test 2");
        board2.setOwner(boardMemberOwner2);
        board2.setMembers(List.of(member21, member22));
        board2.setTaskCategories(List.of(board2Category));

        board1 = boardRepository.save(board1);
        board2 = boardRepository.save(board2);
    }

    @AfterEach
    void clearEnv() {
        userRepresentationRepository.deleteAll();
        boardRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Create Valid Task Test")
    void createValidTaksTest() {

        createTaskRequest.setAssignedTo(Set.of(member11Id));

        TaskShortDto taskDto = taskService.createTask(owner1Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);

        assertNotNull(taskDto);
        assertNotNull(taskDto.getTaskStatus());
        assertNotNull(taskDto.getAssignedTo());
        assertEquals(1, taskDto.getAssignedTo().size());

        assertEquals(title, taskDto.getTitle());
        assertEquals(description, taskDto.getDescription());
        assertEquals(deadline, taskDto.getDeadline());

        assertEquals(member11Id, taskDto.getAssignedTo().stream().findFirst().orElse(null));
    }

    @Test
    @DisplayName("Create Valid Task Self Assigning Test")
    void createValidTaskSelfAssigningTest() {

        createTaskRequest.setAssignedTo(Set.of(owner1Id));

        TaskShortDto taskDto = taskService.createTask(owner1Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);
        assertNotNull(taskDto);
        assertNotNull(taskDto.getTitle());
        assertNotNull(taskDto.getDescription());
        assertNotNull(taskDto.getTaskStatus());
        assertNotNull(taskDto.getDeadline());
        assertNotNull(taskDto.getAssignedTo());

        assertEquals(title, taskDto.getTitle());
        assertEquals(description, taskDto.getDescription());
        assertEquals(deadline, taskDto.getDeadline());
        assertEquals(TaskStatus.UNCOMPLETED, taskDto.getTaskStatus());

        assertFalse(taskDto.getAssignedTo().isEmpty());
        assertEquals(1, taskDto.getAssignedTo().size());
        assertEquals(owner1Id, taskDto.getAssignedTo().stream().findFirst().orElse(null));
    }

    @Test
    @DisplayName("Can Create Task With Full Assignment Test")
    void canCreateTaskWithFullAssignmentTest() {

        createTaskRequest.setAssignedTo(Set.of(owner1Id, member11Id, member12Id));

        TaskShortDto taskDto = taskService.createTask(owner1Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);

        assertNotNull(taskDto);
        assertNotNull(taskDto.getTitle());
        assertNotNull(taskDto.getDescription());
        assertNotNull(taskDto.getTaskStatus());
        assertNotNull(taskDto.getDeadline());
        assertNotNull(taskDto.getAssignedTo());

        assertEquals(title, taskDto.getTitle());
        assertEquals(description, taskDto.getDescription());
        assertEquals(deadline, taskDto.getDeadline());
        assertEquals(TaskStatus.UNCOMPLETED, taskDto.getTaskStatus());

        assertFalse(taskDto.getAssignedTo().isEmpty());
        assertEquals(3, taskDto.getAssignedTo().size());
        assertEquals(owner1Id, taskDto.getCreatedBy());
    }

    @Test
    @DisplayName("Create Task By Member With Permissions Test")
    void createTaskByMemberWithPermissionsTest() {

        createTaskRequest.setAssignedTo(Set.of(member11Id));

        TaskShortDto taskDto = taskService.createTask(member11Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);

        assertNotNull(taskDto);
        assertNotNull(taskDto.getTaskStatus());
        assertNotNull(taskDto.getAssignedTo());
        assertEquals(1, taskDto.getAssignedTo().size());

        assertEquals(title, taskDto.getTitle());
        assertEquals(description, taskDto.getDescription());
        assertEquals(deadline, taskDto.getDeadline());

        assertEquals(member11Id, taskDto.getAssignedTo().stream().findFirst().orElse(null));
    }

    @Test
    @DisplayName("Create Task By Member With Permissions With Members Test")
    void createTaskByMemberWithPermissionsWithMembersTest() {

        createTaskRequest.setAssignedTo(Set.of(member11Id, member12Id));

        TaskShortDto taskDto = taskService.createTask(member11Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);

        assertNotNull(taskDto);
        assertNotNull(taskDto.getTaskStatus());
        assertNotNull(taskDto.getAssignedTo());
        assertEquals(2, taskDto.getAssignedTo().size());

        assertEquals(title, taskDto.getTitle());
        assertEquals(description, taskDto.getDescription());
        assertEquals(deadline, taskDto.getDeadline());

        assertEquals(member11Id, taskDto.getCreatedBy());
    }

    @Test
    @DisplayName("Check Position In Category Increment Test")
    void checkPositionInCategoryIncrementTest() {

        createTaskRequest.setAssignedTo(Set.of(owner1Id));
        TaskShortDto taskDto = taskService.createTask(owner1Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);

        assertEquals(0, taskDto.getPositionInCategory());

        taskService.createTask(owner1Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);

        Query query = new Query(
                Criteria.where("_id").is(new ObjectId(taskDto.getId()))
        );
        Task prevTask = mongoTemplate.findOne(query, Task.class, "tasks");
        assertNotNull(prevTask);
        assertEquals(1, prevTask.getPositionInCategory());
    }

    @Test
    @DisplayName("Create Task Member Not In Board Test")
    void createTaskMemberNotInBoardTest() {

        CreateTaskRequest createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("Task");
        createTaskRequest.setDeadline(Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));
        createTaskRequest.setAssignedTo(Set.of(member21Id));

        assertThrows(RuntimeException.class, () -> taskService.createTask(owner1Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Create Task Invalid Board Id Test")
    void createTaskInvalidBoardIdTest() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest();
        String invalidBoardId = "edcwegw345g3wedfw3rg3wr";
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(owner1Id, invalidBoardId, board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Create Task Board Not Found Test")
    void createTaskBoardNotFoundTest() {

        ObjectId boardId = ObjectId.get();
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(owner1Id, boardId.toHexString(), board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Create Task Category Not Found Test")
    void createTaskCategoryNotFoundTest() {
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(owner1Id, board1.getId().toHexString(), "X", createTaskRequest));
    }

    @Test
    @DisplayName("Create Task User Not Found Test")
    void createTaskUserNotFoundTest() {
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(UUID.randomUUID(), board1.getId().toHexString(), board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Create Task User Not Owner Or Member Test")
    void createTaskUserNotOwnerOrMemberTest() {
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(owner2Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest));
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(member21Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Create Task Board Owner Deleted Test")
    void createTaskBoardOwnerDeletedTest() {
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(owner3Id, ObjectId.get().toHexString(), board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Member Cannot Assign Board Owner Test")
    void cannotAssignBoardOwnerTest() {

        createTaskRequest.setAssignedTo(Set.of(member11Id, owner1Id));
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(member11Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Member Cannot Assign Non Board Members Test")
    void memberCannotAssignNonBoardMembersTest() {

        createTaskRequest.setAssignedTo(Set.of(member11Id, member12Id, member21Id));
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(member11Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest));
    }

    @Test
    @DisplayName("Member Without Permissions Cannot Assign Test")
    void memberWithoutPermissionsCannotAssignTest() {

        createTaskRequest.setAssignedTo(Set.of(member11Id));
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(member12Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest));
    }
}
