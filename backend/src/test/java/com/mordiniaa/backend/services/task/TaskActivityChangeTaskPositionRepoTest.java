package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import com.mordiniaa.backend.models.task.activity.TaskCategoryChange;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.request.task.UpdateTaskPositionRequest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class TaskActivityChangeTaskPositionRepoTest {

    @MockitoBean("mongoAuditor")
    private AuditorAware<String> mongoAuditor;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepresentationRepository userRepresentationRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskActivityService taskActivityService;

    private static final UUID ownerId = UUID.randomUUID();
    private static final UUID member1Id = UUID.randomUUID();
    private static final UUID member2Id = UUID.randomUUID();

    private static UserRepresentation owner;
    private static UserRepresentation user1;
    private static UserRepresentation user2;

    private BoardMember ownerMember;
    private BoardMember member1;
    private BoardMember member2;

    private static TaskCategory taskCategory1;
    private static TaskCategory taskCategory2;

    private static Board board;
    private static Task task1;
    private static Task task2;
    private static Task task3;

    @BeforeAll
    static void setup() {

        owner = new UserRepresentation();
        owner.setUserId(ownerId);
        owner.setUsername("Owner");
        owner.setImageKey("http:random123.com");

        user1 = new UserRepresentation();
        user1.setUserId(member1Id);
        user1.setUsername("Member 1");
        user1.setImageKey("http:random123.com");

        user2 = new UserRepresentation();
        user2.setUserId(member2Id);
        user2.setUsername("Member 2");
        user2.setImageKey("http:random123.com");

        taskCategory1 = new TaskCategory();
        taskCategory1.setCategoryName("Dev");
        taskCategory1.setPosition(0);

        taskCategory2 = new TaskCategory();
        taskCategory2.setCategoryName("Test");
        taskCategory2.setPosition(1);

        board = new Board();
        board.setBoardName("Board");
        board.setTaskCategories(List.of(taskCategory1, taskCategory2));

        Instant deadline = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);

        task1 = new Task();
        task1.setCreatedBy(ownerId);
        task1.setTitle("Dev 0");
        task1.setPositionInCategory(0);
        task1.setDeadline(deadline);
        task1.setDescription("Test Task Description 1");

        task2 = new Task();
        task2.setCreatedBy(member1Id);
        task2.setTitle("Dev 1");
        task2.setPositionInCategory(1);
        task2.setDeadline(deadline);
        task2.setDescription("Test Task Description 2");

        task3 = new Task();
        task3.setCreatedBy(member2Id);
        task3.setTitle("Test 0");
        task3.setPositionInCategory(0);
        task3.setDeadline(deadline);
        task3.setDescription("Dev Task Description 1");
    }

    @BeforeEach
    void beforeEachSetup() {

        userRepresentationRepository.saveAll(List.of(owner, user1, user2));
        task1 = taskRepository.save(task1);
        task2 = taskRepository.save(task2);
        task3 = taskRepository.save(task3);

        taskCategory1.getTasks().addAll(Set.of(task1.getId(), task2.getId()));
        taskCategory2.getTasks().add(task3.getId());

        ownerMember = new BoardMember(ownerId);
        member1 = new BoardMember(member1Id);
        member2 = new BoardMember(member2Id);
        board.setOwner(ownerMember);
        board.setTeamId(UUID.randomUUID());
        board.setMembers(List.of(member1, member2));

        board = boardRepository.save(board);
    }

    @AfterEach
    void afterEachClearing() {

        userRepresentationRepository.deleteAll();
        taskRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("Increase Task Position Valid Test")
    void increaseTaskPositionValidTest() {

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        UpdateTaskPositionRequest positionRequest = new UpdateTaskPositionRequest();
        positionRequest.setNewPosition(1);

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);

        TaskShortDto dto = taskActivityService.changeTaskPosition(ownerId, boardId, taskId, positionRequest);
        assertNotNull(dto);
        assertEquals(taskId, dto.getId());
        assertEquals(1, dto.getPositionInCategory());

        Task task = taskService.findTaskById(task2.getId());
        assertEquals(0, task.getPositionInCategory());
    }

    @Test
    @DisplayName("Decrease Task Position Valid Test")
    void decreaseTaskPositionValidTest() {

        String boardId = board.getId().toHexString();
        String taskId = task2.getId().toHexString();

        UpdateTaskPositionRequest positionRequest = new UpdateTaskPositionRequest();
        positionRequest.setNewPosition(0);

        member1.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);

        TaskShortDto dto = taskActivityService.changeTaskPosition(member1Id, boardId, taskId, positionRequest);
        assertNotNull(dto);
        assertEquals(taskId, dto.getId());
        assertEquals(0, dto.getPositionInCategory());

        Task task = taskService.findTaskById(task1.getId());
        assertEquals(1, task.getPositionInCategory());
    }

    @Test
    @DisplayName("Move Task Between Categories Position 1 Test")
    void moveTaskBetweenCategoriesTest() {

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        UpdateTaskPositionRequest positionRequest = new UpdateTaskPositionRequest();
        positionRequest.setNewPosition(1);
        positionRequest.setNewTaskCategory(taskCategory2.getCategoryName());

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        ownerMember.setCategoryPermissions(Set.of(CategoryPermissions.MOVE_TASK_BETWEEN_CATEGORIES));
        boardRepository.save(board);

        TaskShortDto dto = taskActivityService.changeTaskPosition(
                ownerId,
                boardId,
                taskId,
                positionRequest
        );

        assertNotNull(dto);
        assertEquals(1, dto.getPositionInCategory());

        Task task = taskService.findTaskById(task3.getId());
        assertEquals(0, task.getPositionInCategory());

        Task prevCatTask = taskService.findTaskById(task2.getId());
        assertEquals(0, prevCatTask.getPositionInCategory());

        Board dbBoard = boardRepository.findById(board.getId())
                .orElseThrow(RuntimeException::new);

        TaskCategory nextCat = dbBoard.getTaskCategories().stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategory2.getCategoryName()))
                .findFirst().orElseThrow(RuntimeException::new);

        TaskCategory prevCat = dbBoard.getTaskCategories().stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategory1.getCategoryName()))
                .findFirst().orElseThrow();

        assertTrue(nextCat.getTasks().contains(task.getId()));
        assertFalse(prevCat.getTasks().contains(task.getId()));

        Task movedTask = taskService.findTaskById(task1.getId());
        assertNotNull(movedTask.getActivityElements());
        assertFalse(movedTask.getActivityElements().isEmpty());

        TaskActivityElement activityElement = movedTask.getActivityElements().getFirst();
        assertInstanceOf(TaskCategoryChange.class, activityElement);
        TaskCategoryChange categoryChange = (TaskCategoryChange) activityElement;
        assertEquals(taskCategory1.getCategoryName(), categoryChange.getPrevCategory());
        assertEquals(taskCategory2.getCategoryName(), categoryChange.getNextCategory());
        assertEquals(ownerId, categoryChange.getUser());
    }

    @Test
    @DisplayName("Move Task Between Categories Position 0 Test")
    void moveTaskBetweenCategoriesPt2Test() {

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        UpdateTaskPositionRequest positionRequest = new UpdateTaskPositionRequest();
        positionRequest.setNewPosition(0);
        positionRequest.setNewTaskCategory(taskCategory2.getCategoryName());

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        ownerMember.setCategoryPermissions(Set.of(CategoryPermissions.MOVE_TASK_BETWEEN_CATEGORIES));
        boardRepository.save(board);

        TaskShortDto dto = taskActivityService.changeTaskPosition(
                ownerId,
                boardId,
                taskId,
                positionRequest
        );

        assertNotNull(dto);
        assertEquals(0, dto.getPositionInCategory());

        Task task = taskService.findTaskById(task3.getId());
        assertEquals(1, task.getPositionInCategory());

        Task prevCatTask = taskService.findTaskById(task2.getId());
        assertEquals(0, prevCatTask.getPositionInCategory());

        Board dbBoard = boardRepository.findById(board.getId())
                .orElseThrow(RuntimeException::new);

        TaskCategory nextCat = dbBoard.getTaskCategories().stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategory2.getCategoryName()))
                .findFirst().orElseThrow(RuntimeException::new);

        TaskCategory prevCat = dbBoard.getTaskCategories().stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategory1.getCategoryName()))
                .findFirst().orElseThrow();

        assertTrue(nextCat.getTasks().contains(task.getId()));
        assertFalse(prevCat.getTasks().contains(task.getId()));
    }

    @Test
    @DisplayName("Move From Cat 2 To Cat 1 Valid Test")
    void moveFromTestToDevCatValidTest() {

        String boardId = board.getId().toHexString();
        String taskId = task3.getId().toHexString();

        UpdateTaskPositionRequest positionRequest = new UpdateTaskPositionRequest();
        positionRequest.setNewPosition(1);
        positionRequest.setNewTaskCategory(taskCategory1.getCategoryName());

        member2.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member2.setCategoryPermissions(Set.of(CategoryPermissions.MOVE_TASK_BETWEEN_CATEGORIES));
        boardRepository.save(board);

        TaskShortDto dto = taskActivityService.changeTaskPosition(member2Id, boardId, taskId, positionRequest);

        assertNotNull(dto);
        assertEquals(1, dto.getPositionInCategory());

        Task firstTask = taskService.findTaskById(task1.getId());
        assertEquals(0, firstTask.getPositionInCategory());

        Task lastTask = taskService.findTaskById(task2.getId());
        assertEquals(2, lastTask.getPositionInCategory());

        Board dbBoard = boardRepository.findById(board.getId())
                .orElseThrow();

        TaskCategory emptyCategory = dbBoard.getTaskCategories()
                .stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategory2.getCategoryName()))
                .findFirst().orElseThrow();

        assertTrue(emptyCategory.getTasks().isEmpty());
    }

    @Test
    @DisplayName("Change Task Position Task Not Found Test")
    void changePositionTaskNotFound() {

        String boardId = board.getId().toHexString();
        String taskId = ObjectId.get().toHexString();

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);

        assertThrows(RuntimeException.class, () -> taskActivityService.changeTaskPosition(
                ownerId, boardId, taskId, new UpdateTaskPositionRequest()
        ));
    }

    @Test
    @DisplayName("Change Task Position Board Not Found Test")
    void changeTaskPositionBoardNotFoundTest() {

        String boardId = ObjectId.get().toHexString();
        String taskId = task1.getId().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.changeTaskPosition(ownerId, boardId, taskId, new UpdateTaskPositionRequest()));
    }

    @Test
    @DisplayName("Change Task Position User Not Found Test")
    void changeTaskPositionUserNotFound() {

        String boardId = ObjectId.get().toHexString();
        String taskId = task1.getId().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.changeTaskPosition(UUID.randomUUID(), boardId, taskId, new UpdateTaskPositionRequest()));
    }

    @Test
    @DisplayName("Change Task Position User Without Board Permission Test")
    void changeTaskPositionUserWithoutBoardPermissionTest() {

        String boardId = board.getId().toHexString();
        String taskId = task2.getId().toHexString();

        member1.setCategoryPermissions(Set.of(CategoryPermissions.MOVE_TASK_BETWEEN_CATEGORIES));
        boardRepository.save(board);

        assertThrows(RuntimeException.class,
                () -> taskActivityService.changeTaskPosition(member1Id, boardId, taskId, new UpdateTaskPositionRequest()));
    }

    @Test
    @DisplayName("Change Task Position User Not Board Member Test")
    void changeTaskPositionUserNotBoardMemberTest() {

        UUID userId = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setUserId(userId);
        user.setUsername("Not Board User");
        user.setImageKey("Https://random123.com");

        userRepresentationRepository.save(user);

        BoardMember member = new BoardMember(userId);
        Board anotherBoard = new Board();
        anotherBoard.setTeamId(UUID.randomUUID());
        anotherBoard.setOwner(member);
        anotherBoard.setBoardName("Another Board");
        boardRepository.save(anotherBoard);

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.changeTaskPosition(userId, boardId, taskId, new UpdateTaskPositionRequest()));
    }

    @Test
    @DisplayName("Change Board Owner Task User Member Test")
    void changeBoardOwnerTaskUserMemberTest() {

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        member1.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);

        assertThrows(RuntimeException.class,
                () -> taskActivityService.changeTaskPosition(member1Id, boardId, taskId, new UpdateTaskPositionRequest()));
    }

    @Test
    @DisplayName("Change Task Position And Category User Not Permitted Test")
    void changeTaskPositionAndCategoryUserNotPermittedTest() {

        String boardId = board.getId().toHexString();
        String taskId = task2.getId().toHexString();

        member1.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        UpdateTaskPositionRequest request = new UpdateTaskPositionRequest();
        request.setNewPosition(1);
        request.setNewTaskCategory(taskCategory2.getCategoryName());

        assertThrows(RuntimeException.class,
                () -> taskActivityService.changeTaskPosition(member1Id, boardId, taskId, request));
    }
}