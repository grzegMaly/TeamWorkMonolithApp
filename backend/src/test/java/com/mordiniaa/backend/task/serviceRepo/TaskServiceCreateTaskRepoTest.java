package com.mordiniaa.backend.task.serviceRepo;

import com.mordiniaa.backend.dto.task.TaskCardDto;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.request.board.task.CreateTaskRequest;
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

@ActiveProfiles("test")
@DataMongoTest
@Import({
        TaskMapper.class,
        TaskService.class
})
public class TaskServiceCreateTaskRepoTest {

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

    private final UUID owner1Id = UUID.randomUUID();
    private final UUID owner2Id = UUID.randomUUID();
    private final UUID owner3Id = UUID.randomUUID();

    private final UUID member11Id = UUID.randomUUID();
    private final UUID member12Id = UUID.randomUUID();
    private final UUID member21Id = UUID.randomUUID();
    private final UUID member22Id = UUID.randomUUID();

    private final String board1CategoryName = "Testing";
    private final String board2CategoryName = "Developing";

    private UserRepresentation boardOwner1;
    private UserRepresentation boardOwner2;
    private UserRepresentation deletedBoardOwner3;
    private BoardMember boardMemberOwner1;
    private BoardMember boardMemberOwner2;
    private BoardMember member11;
    private BoardMember member12;
    private BoardMember member21;
    private BoardMember member22;
    private Board board1;
    private Board board2;

    @BeforeEach
    void setEnv() {

        boardOwner1 = new UserRepresentation();
        boardOwner1.setUserId(owner1Id);
        boardOwner1.setUsername("Owner 1");
        boardOwner1.setImageUrl("https://random1.pl");
        boardOwner1 = userRepresentationRepository.save(boardOwner1);

        boardOwner2 = new UserRepresentation();
        boardOwner2.setUserId(owner2Id);
        boardOwner2.setUsername("Owner 2");
        boardOwner2.setImageUrl("https://random2.pl");
        boardOwner2 = userRepresentationRepository.save(boardOwner2);

        deletedBoardOwner3 = new UserRepresentation();
        deletedBoardOwner3.setUserId(owner3Id);
        deletedBoardOwner3.setUsername("Owner 3");
        deletedBoardOwner3.setImageUrl("https://random3.pl");
        deletedBoardOwner3.setDeleted(true);
        deletedBoardOwner3 = userRepresentationRepository.save(deletedBoardOwner3);

        boardMemberOwner1 = new BoardMember();
        boardMemberOwner1.setUserId(owner1Id);
        boardMemberOwner1.setBoardPermissions(boardPermissions);
        boardMemberOwner1.setTaskPermissions(taskPermissions);

        boardMemberOwner2 = new BoardMember();
        boardMemberOwner2.setUserId(owner2Id);
        boardMemberOwner2.setBoardPermissions(boardPermissions);
        boardMemberOwner2.setTaskPermissions(taskPermissions);

        member11 = new BoardMember();
        member11.setUserId(member11Id);

        member12 = new BoardMember();
        member12.setUserId(member12Id);

        member21 = new BoardMember();
        member21.setUserId(member21Id);

        member22 = new BoardMember();
        member22.setUserId(member22Id);

        TaskCategory board1Category = new TaskCategory();
        board1Category.setCategoryName(board1CategoryName);
        board1 = new Board();
        board1.setBoardName("Test 1");
        board1.setOwner(boardMemberOwner1);
        board1.setMembers(List.of(member11, member12));
        board1.setTaskCategories(List.of(board1Category));

        TaskCategory board2Category = new TaskCategory();
        board2Category.setCategoryName(board2CategoryName);
        board2 = new Board();
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
    void createValidTaksTest() {

        String title = "Task1";
        String description = "Task Task";
        Instant deadline = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);

        CreateTaskRequest createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle(title);
        createTaskRequest.setDeadline(deadline);
        createTaskRequest.setDescription(description);
        createTaskRequest.setAssignedTo(Set.of(member11Id));

        TaskCardDto taskDto = taskService.createTask(owner1Id, board1.getId().toHexString(), board1CategoryName, createTaskRequest);

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
                () -> taskService.createTask(owner1Id, boardId.toHexString(), board1CategoryName, new CreateTaskRequest()));
    }

    @Test
    @DisplayName("Create Task Category Not Found Test")
    void createTaskCategoryNotFoundTest() {
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(owner1Id, board1.getId().toHexString(), "X", new CreateTaskRequest()));
    }

    @Test
    @DisplayName("Create Task User Not Found Test")
    void createTaskUserNotFoundTest() {
        assertThrows(RuntimeException.class,
                () -> taskService.createTask(UUID.randomUUID(), board1.getId().toHexString(), board1CategoryName, new CreateTaskRequest()));
    }
}
