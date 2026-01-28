package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.mappers.User.UserRepresentationMapper;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.TaskActivityMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskCommentDtoMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.user.aggregation.UserReprCustomRepositoryImpl;
import com.mordiniaa.backend.request.task.AssignUsersRequest;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.With;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.security.Permission;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataMongoTest
@Import({
        BoardAggregationRepositoryImpl.class,
        UserReprCustomRepositoryImpl.class,
        MongoUserService.class,
        BoardUtils.class,
        MongoIdUtils.class,
        TaskMapper.class,
        TaskService.class,
        TaskManagementService.class,
        TaskCommentDtoMapper.class,
        TaskActivityMapper.class,
        UserRepresentationMapper.class
})
public class TaskManagementRemoveUserFromTaskRepoTest {

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
    private TaskManagementService taskManagementService;

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

    private static Board boardTemplate;
    private static Board board;
    private static Task task1;
    private static Task task2;
    private static Task task3;

    @BeforeAll
    static void setup() {

        owner = new UserRepresentation();
        owner.setUserId(ownerId);
        owner.setUsername("Owner");
        owner.setImageUrl("http:random123.com");

        user1 = new UserRepresentation();
        user1.setUserId(member1Id);
        user1.setUsername("Member 1");
        user1.setImageUrl("http:random123.com");

        user2 = new UserRepresentation();
        user2.setUserId(member2Id);
        user2.setUsername("Member 2");
        user2.setImageUrl("http:random123.com");

        taskCategory1 = new TaskCategory();
        taskCategory1.setCategoryName("Dev");
        taskCategory1.setPosition(0);

        taskCategory2 = new TaskCategory();
        taskCategory2.setCategoryName("Test");
        taskCategory2.setPosition(1);

        boardTemplate = new Board();
        boardTemplate.setBoardName("Board");
        boardTemplate.setTaskCategories(List.of(taskCategory1, taskCategory2));

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
        boardTemplate.setOwner(ownerMember);
        boardTemplate.setMembers(new ArrayList<>(List.of(member1, member2)));

        board = boardRepository.save(boardTemplate);
    }

    @AfterEach
    void afterEachClearing() {

        userRepresentationRepository.deleteAll();
        taskRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Task Owner Can Unassign User From Owned Task")
    void taskOwnerCanUnassignUserFromOwnedTask() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        this.setAssignmentPermissionForMember(member1);
        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member2Id));
        taskManagementService.assignUsersToTask(member1Id, request, bId, tId);

        assertDoesNotThrow(() -> taskManagementService.removeUserFromTask(
                member1Id,
                member2Id,
                bId,
                tId
        ));
    }

    @Test
    @Order(2)
    @DisplayName("Board Owner Can Unassign User From Owned Task")
    void boardOwnerCanUnassignUserFromOwnedTask() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        this.setAssignmentPermissionForMember(ownerMember);
        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, member2Id));
        assertDoesNotThrow(() -> taskManagementService.assignUsersToTask(
                ownerId,
                request,
                bId,
                tId
        ));

        assertDoesNotThrow(() -> taskManagementService.removeUserFromTask(
                ownerId,
                member1Id,
                bId,
                tId
        ));

        Task task = taskService.findTaskById(task1.getId());
        assertEquals(1, task.getAssignedTo().size());
        assertTrue(task.getAssignedTo().contains(member2Id));
    }

    @Test
    @Order(3)
    @DisplayName("Board Owner Can Unassign User From Any Task")
    void boardOwnerCanUnassignUserFromAnyTask() {

        String bId = board.getId().toHexString();
        String tId = task3.getId().toHexString();

        this.setAssignmentPermissionForMember(ownerMember);
        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, member2Id));
        assertDoesNotThrow(() -> taskManagementService.assignUsersToTask(
                ownerId,
                request,
                bId,
                tId
        ));

        assertDoesNotThrow(() -> taskManagementService.removeUserFromTask(
                ownerId,
                member2Id,
                bId,
                tId
        ));

        Task task = taskService.findTaskById(task3.getId());
        assertEquals(1, task.getAssignedTo().size());
        assertTrue(task.getAssignedTo().contains(member1Id));
    }

    @Test
    @Order(4)
    @DisplayName("Board Member With Permission Can Unassign User From Any Task Apart From BOwner And TOwner")
    void boardMemberWithPermissionCanUnassignUserFromAnyTaskApartFromBOwnerAndTOwner() {

        String bId = board.getId().toHexString();
        String tId = task3.getId().toHexString();

        UUID userId = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setUserId(userId);
        user.setUsername("Username");
        user.setImageUrl("https://random123.com");
        userRepresentationRepository.save(user);

        BoardMember newMember = new BoardMember(userId);
        board.setMembers(List.of(member1, member2, newMember));
        boardRepository.save(board);

        this.setAssignmentPermissionForMember(member1);

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, userId));

        assertDoesNotThrow(() -> taskManagementService.assignUsersToTask(
                member1Id,
                request,
                bId,
                tId
        ));

        member1.setTaskPermissions(Set.of(TaskPermission.UNASSIGN_TASK));
        boardRepository.save(board);

        assertDoesNotThrow(() -> taskManagementService.removeUserFromTask(
                member1Id,
                userId,
                bId,
                tId
        ));

        Task task = taskService.findTaskById(task3.getId());
        assertEquals(1, task.getAssignedTo().size());
        assertTrue(task.getAssignedTo().contains(member1Id));
    }

    @Test
    @Order(5)
    @DisplayName("Board Not Found")
    void boardNotFoundTest() {

        String bId = ObjectId.get().toHexString();
        String tId = task2.getId().toHexString();

        assertThrows(RuntimeException.class, () -> taskManagementService.removeUserFromTask(
                member1Id,
                member2Id,
                bId,
                tId
        ));
    }

    @Test
    @Order(6)
    @DisplayName("Task Not Found")
    void taskNotFoundTest() {

        String bId = board.getId().toHexString();
        String tId = ObjectId.get().toHexString();

        assertThrows(RuntimeException.class, () -> taskManagementService.removeUserFromTask(
                ownerId,
                member1Id,
                bId,
                tId
        ));
    }

    @Test
    @Order(7)
    @DisplayName("User Not Found")
    void userNotFoundTest() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        assertThrows(RuntimeException.class, () -> taskManagementService.removeUserFromTask(
                ownerId,
                UUID.randomUUID(),
                bId,
                tId
        ));
    }

    private void setAssignmentPermissionForMember(BoardMember member) {

        member.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member.setTaskPermissions(Set.of(TaskPermission.CREATE_TASK, TaskPermission.ASSIGN_TASK));
        boardRepository.save(board);
    }
}
