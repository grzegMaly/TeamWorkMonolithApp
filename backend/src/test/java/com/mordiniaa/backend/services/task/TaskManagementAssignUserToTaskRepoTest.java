package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.request.task.AssignUsersRequest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class TaskManagementAssignUserToTaskRepoTest {

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

    private static UserRepresentation ownerTemplate;
    private static UserRepresentation user1Template;
    private static UserRepresentation user2Template;

    private UserRepresentation owner;
    private UserRepresentation user1;
    private UserRepresentation user2;

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

        ownerTemplate = new UserRepresentation();
        ownerTemplate.setUserId(ownerId);
        ownerTemplate.setUsername("Owner");
        ownerTemplate.setDeleted(false);
        ownerTemplate.setImageKey("http:random123.com");

        user1Template = new UserRepresentation();
        user1Template.setUserId(member1Id);
        user1Template.setUsername("Member 1");
        user1Template.setDeleted(false);
        user1Template.setImageKey("http:random123.com");

        user2Template = new UserRepresentation();
        user2Template.setUserId(member2Id);
        user2Template.setUsername("Member 2");
        user2Template.setDeleted(false);
        user2Template.setImageKey("http:random123.com");

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

        ownerTemplate.setDeleted(false);
        user1Template.setDeleted(false);
        user2Template.setDeleted(false);
        owner = userRepresentationRepository.save(ownerTemplate);
        user1 = userRepresentationRepository.save(user1Template);
        user2 = userRepresentationRepository.save(user2Template);
        task1 = taskRepository.save(task1);
        task2 = taskRepository.save(task2);
        task3 = taskRepository.save(task3);

        taskCategory1.getTasks().addAll(Set.of(task1.getId(), task2.getId()));
        taskCategory2.getTasks().add(task3.getId());

        ownerMember = new BoardMember(ownerId);
        member1 = new BoardMember(member1Id);
        member2 = new BoardMember(member2Id);
        boardTemplate.setOwner(ownerMember);
        boardTemplate.setTeamId(UUID.randomUUID());
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
    @DisplayName("Task Owner Can Assign Board Member To Owner Task Test")
    void taskOwnerCanAssignBoardMemberToOwnerTask() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member2Id));

        setAssignmentPermissionForMember(member1);
        TaskDetailsDTO dto = taskManagementService.assignUsersToTask(
                member1Id,
                request,
                bId,
                tId
        );

        assertNotNull(dto);
        assertFalse(dto.getAssignedTo().isEmpty());

        assertEquals(1, dto.getAssignedTo().size());
        assertTrue(dto.getAssignedTo().contains(member2Id));
    }

    @Test
    @Order(2)
    @DisplayName("Board Owner Can Assign Any Board Member To Any Task")
    void boardOwnerCanAssignAnyBoardMemberToAnyTask() {

        String bId = board.getId().toHexString();
        String tId = task3.getId().toHexString();

        this.setAssignmentPermissionForMember(ownerMember);
        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, member2Id));

        TaskDetailsDTO dto = taskManagementService.assignUsersToTask(
                ownerId,
                request,
                bId,
                tId
        );

        assertNotNull(dto);
        assertEquals(2, dto.getAssignedTo().size());
        assertTrue(dto.getAssignedTo().containsAll(Set.of(member1Id, member2Id)));
    }

    @Test
    @Order(3)
    @DisplayName("Board Member With Permission Can Assign Any Member To Task Apart From BOwner And TOwner")
    void boardMemberWithPermissionCanAssignAnyMemberToTaskApartFromBOwnerAndTOwner() {

        UUID userId = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setUserId(userId);
        user.setUsername("Username");
        user.setImageKey("https://random123.com");
        userRepresentationRepository.save(user);

        BoardMember boardMember = new BoardMember(userId);
        board.getMembers().add(boardMember);
        this.setAssignmentPermissionForMember(boardMember);

        String bId = board.getId().toHexString();
        String tId = task3.getId().toHexString();

        AssignUsersRequest request1 = new AssignUsersRequest();
        request1.setUsers(Set.of(member1Id));
        TaskDetailsDTO dto = taskManagementService.assignUsersToTask(
                userId,
                request1,
                bId,
                tId
        );
        assertNotNull(dto);
        assertEquals(1, dto.getAssignedTo().size());
        assertTrue(dto.getAssignedTo().contains(member1Id));

        /*CANNOT ASSIGN BOWNER*/
        AssignUsersRequest request2 = new AssignUsersRequest();
        request2.setUsers(Set.of(ownerId));
        assertThrows(RuntimeException.class,
                () -> taskManagementService.assignUsersToTask(
                        userId,
                        request2,
                        bId,
                        tId
                ));

        /*CANNOT ASSIGN TOWNER*/
        AssignUsersRequest request3 = new AssignUsersRequest();
        request3.setUsers(Set.of(member2Id));
        assertThrows(RuntimeException.class,
                () -> taskManagementService.assignUsersToTask(
                        userId,
                        request3,
                        bId,
                        tId
                ));

        /*CANNOT ASSIGN BOTH*/
        AssignUsersRequest request4 = new AssignUsersRequest();
        request4.setUsers(Set.of(member2Id, ownerId));
        assertThrows(RuntimeException.class,
                () -> taskManagementService.assignUsersToTask(
                        userId,
                        request4,
                        bId,
                        tId
                ));
    }

    @Test
    @Order(4)
    @DisplayName("Task Owner Can Assign Self To Owned Task")
    void tskOwnerCanAssignSelfToOwnedTask() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id));

        member1.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);

        TaskDetailsDTO dto = taskManagementService.assignUsersToTask(
                member1Id,
                request,
                bId,
                tId
        );

        assertNotNull(dto);
        assertEquals(1, dto.getAssignedTo().size());
        assertTrue(dto.getAssignedTo().contains(member1Id));
    }

    @Test
    @Order(5)
    @DisplayName("Board Owner Can Assign Self To Owned Task")
    void boardOwnerCanAssignSelfToOwnedTask() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(ownerId));

        TaskDetailsDTO dto = taskManagementService.assignUsersToTask(
                ownerId,
                request,
                bId,
                tId
        );

        assertNotNull(dto);
        assertFalse(dto.getAssignedTo().isEmpty());
        assertEquals(1, dto.getAssignedTo().size());
        assertTrue(dto.getAssignedTo().contains(ownerId));
    }

    @Test
    @Order(6)
    @DisplayName("Task Owner Can Assign Self To Owned Task With Members")
    void taskOwnerCanAssignSelfToOwnedTaskWithMembers() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        this.setAssignmentPermissionForMember(member1);

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, member2Id));

        TaskDetailsDTO dto = taskManagementService.assignUsersToTask(
                member1Id,
                request,
                bId,
                tId
        );
        assertNotNull(dto);
        assertFalse(dto.getAssignedTo().isEmpty());
        assertEquals(2, dto.getAssignedTo().size());
        assertTrue(dto.getAssignedTo().containsAll(Set.of(member1Id, member2Id)));
    }

    @Test
    @Order(7)
    @DisplayName("Board Owner Can Assign Self To Owned Task With Members")
    void boardOwnerCanAssignSelfToOwnedTaskWithMembers() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        this.setAssignmentPermissionForMember(ownerMember);

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, member2Id, ownerId));

        TaskDetailsDTO dto = taskManagementService.assignUsersToTask(
                ownerId,
                request,
                bId,
                tId
        );
        assertNotNull(dto);
        assertFalse(dto.getAssignedTo().isEmpty());
        assertEquals(3, dto.getAssignedTo().size());
        assertTrue(dto.getAssignedTo().containsAll(Set.of(member1Id, member2Id, ownerId)));
    }

    @Test
    @Order(8)
    @DisplayName("Board Not Found")
    void boardNotFound() {

        String bId = ObjectId.get().toHexString();
        String tId = task1.getId().toHexString();

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id));

        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                ownerId,
                request,
                bId,
                tId
        ));
    }

    @Test
    @Order(9)
    @DisplayName("Task Not Found")
    void taskNotFoundTest() {

        String bId = board.getId().toHexString();
        String tId = ObjectId.get().toHexString();
        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id));

        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                ownerId,
                request,
                bId,
                tId
        ));
    }

    @Test
    @Order(10)
    @DisplayName("User Not Found")
    void userNotFoundTest() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, member2Id));
        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                UUID.randomUUID(),
                request,
                bId,
                tId
        ));
    }

    @Test
    @Order(11)
    @DisplayName("Current User Not Board User")
    void currentUserNotBoardUserTest() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        UUID userId = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setUserId(userId);
        user.setUsername("Username");
        user.setImageKey("https://random123.com");
        userRepresentationRepository.save(user);

        BoardMember member = new BoardMember(userId);
        member.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member.setTaskPermissions(Set.of(TaskPermission.CREATE_TASK, TaskPermission.ASSIGN_TASK));

        Board newBoard = new Board();
        newBoard.setTeamId(UUID.randomUUID());
        newBoard.setBoardName("Boardname");
        newBoard.setOwner(member);
        boardRepository.save(newBoard);

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id, member2Id));

        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                userId,
                request,
                bId,
                tId
        ));
    }

    @Test
    @Order(12)
    @DisplayName("Current User Inactive")
    void currentUserInactiveTest() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        this.setAssignmentPermissionForMember(member1);
        user1.setDeleted(true);
        userRepresentationRepository.save(user1);

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member2Id));

        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                member1Id,
                request,
                bId,
                tId
        ));
    }

    @Test
    @Order(13)
    @DisplayName("User To Assign Inactive")
    void userToAssignInactiveTest() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        user2.setDeleted(true);
        userRepresentationRepository.save(user2);

        this.setAssignmentPermissionForMember(member1);

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member2Id));

        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                member1Id,
                request,
                bId,
                tId
        ));
    }

    @Test
    @Order(14)
    @DisplayName("Board Member Assigning To Task Without Permission")
    void boardMemberAssigningToTaskWithoutPermission() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member1Id));

        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                member2Id,
                request,
                bId,
                tId
        ));
        member2.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);
        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                member2Id,
                request,
                bId,
                tId
        ));
    }

    @Test
    @Order(15)
    @DisplayName("Board Member Assigning Self To Task Without Permission")
    void boardMemberAssigningSelfToTaskWithoutPermission() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        AssignUsersRequest request = new AssignUsersRequest();
        request.setUsers(Set.of(member2Id));

        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                member2Id,
                request,
                bId,
                tId
        ));
        member2.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardRepository.save(board);
        assertThrows(RuntimeException.class, () -> taskManagementService.assignUsersToTask(
                member2Id,
                request,
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
