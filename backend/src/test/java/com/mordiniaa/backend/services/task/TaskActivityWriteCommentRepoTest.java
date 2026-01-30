package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.activity.TaskActivityElementDto;
import com.mordiniaa.backend.dto.task.activity.TaskCommentDto;
import com.mordiniaa.backend.dto.user.mongodb.UserDto;
import com.mordiniaa.backend.mappers.User.UserRepresentationMapper;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.TaskActivityMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskCommentDtoMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.activity.TaskComment;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.user.aggregation.UserReprCustomRepositoryImpl;
import com.mordiniaa.backend.request.task.UploadCommentRequest;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
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
        BoardAggregationRepositoryImpl.class,
        UserReprCustomRepositoryImpl.class,
        MongoUserService.class,
        BoardUtils.class,
        MongoIdUtils.class,
        TaskMapper.class,
        TaskService.class,
        TaskActivityService.class,
        TaskCommentDtoMapper.class,
        TaskActivityMapper.class,
        UserRepresentationMapper.class
})
public class TaskActivityWriteCommentRepoTest {

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
        boardTemplate.setTeamId(UUID.randomUUID());
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
        boardTemplate.setMembers(List.of(member1, member2));

        board = boardRepository.save(boardTemplate);
    }

    @AfterEach
    void afterEachClearing() {

        userRepresentationRepository.deleteAll();
        taskRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("Write Comment Valid Test")
    void writeCommentValidTest() {

        String boardId = boardTemplate.getId().toHexString();
        String taskId = task1.getId().toHexString();

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        ownerMember.setCommentPermissions(Set.of(CommentPermission.COMMENT_TASK));
        boardRepository.save(board);

        String comment = "Test Comment By owner";

        UploadCommentRequest commentRequest = new UploadCommentRequest();
        commentRequest.setComment(comment);

        TaskDetailsDTO dto = taskActivityService.writeComment(
                ownerId,
                boardId,
                taskId,
                commentRequest
        );

        assertNotNull(dto);

        assertEquals(taskId, dto.getId());
        assertFalse(dto.getTaskActivityElements().isEmpty());

        TaskActivityElementDto element = dto.getTaskActivityElements().getFirst();
        assertInstanceOf(TaskCommentDto.class, element);

        TaskCommentDto commentDto = (TaskCommentDto) element;
        assertNotNull(commentDto);
        assertEquals(comment, commentDto.getComment());
        assertNotNull(commentDto.getCommentId());
        assertFalse(commentDto.isUpdated());

        Task dbTask = taskService.findTaskById(task1.getId());
        assertEquals(commentDto.getCommentId(), ((TaskComment) dbTask.getActivityElements().getFirst()).getCommentId());
    }

    @Test
    @DisplayName("Write Comment Member Valid Test")
    void writeCommentMemberValidTest() {

        task1.setAssignedTo(Set.of(member1Id));
        taskRepository.save(task1);

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        member1.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member1.setCommentPermissions(Set.of(CommentPermission.COMMENT_TASK));

        boardRepository.save(board);

        String comment = "Test Comment By owner";

        UploadCommentRequest commentRequest = new UploadCommentRequest();
        commentRequest.setComment(comment);

        TaskDetailsDTO dto = taskActivityService.writeComment(
                member1Id,
                boardId,
                taskId,
                commentRequest
        );

        assertNotNull(dto);

        assertEquals(taskId, dto.getId());
        assertFalse(dto.getTaskActivityElements().isEmpty());

        TaskActivityElementDto element = dto.getTaskActivityElements().getFirst();
        assertInstanceOf(TaskCommentDto.class, element);

        TaskCommentDto commentDto = (TaskCommentDto) element;
        assertNotNull(commentDto);
        assertEquals(comment, commentDto.getComment());
        assertNotNull(commentDto.getCommentId());
        assertFalse(commentDto.isUpdated());

        Task dbTask = taskService.findTaskById(task1.getId());
        assertEquals(commentDto.getCommentId(), ((TaskComment) dbTask.getActivityElements().getFirst()).getCommentId());
    }

    @Test
    @DisplayName("Board Owner Post Any Comment Test")
    void boardOwnerPostAnyCommentTest() {

        String boardId = board.getId().toHexString();
        String taskId = task2.getId().toHexString();

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        ownerMember.setCommentPermissions(Set.of(CommentPermission.COMMENT_TASK));
        boardRepository.save(board);

        String comment = "Comment";

        UploadCommentRequest request = new UploadCommentRequest();
        request.setComment(comment);

        TaskDetailsDTO dto = taskActivityService.writeComment(
                ownerId,
                boardId,
                taskId,
                request
        );
        assertNotNull(dto);

        assertEquals(taskId, dto.getId());
        assertFalse(dto.getTaskActivityElements().isEmpty());

        TaskActivityElementDto element = dto.getTaskActivityElements().getFirst();
        assertInstanceOf(TaskCommentDto.class, element);

        TaskCommentDto commentDto = (TaskCommentDto) element;
        assertNotNull(commentDto);
        assertEquals(comment, commentDto.getComment());
        assertNotNull(commentDto.getCommentId());
        assertFalse(commentDto.isUpdated());

        Task dbTask = taskService.findTaskById(task2.getId());
        assertEquals(commentDto.getCommentId(), ((TaskComment) dbTask.getActivityElements().getFirst()).getCommentId());

        UserDto uDto = commentDto.getUser();
        assertNotNull(uDto);
        assertEquals(ownerId, uDto.getUserId());
    }

    @Test
    @DisplayName("Upload Comment Board Not Found Test")
    void uploadCommentBoardNotFound() {

        String boardId = ObjectId.get().toHexString();
        String taskId = task1.getId().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.writeComment(
                        ownerId,
                        boardId,
                        taskId,
                        new UploadCommentRequest()
                ));
    }

    @Test
    @DisplayName("Upload Comment Task Not Found Test")
    void uploadCommentTaskNotFound() {

        String boardId = board.getId().toHexString();
        String taskIsd = ObjectId.get().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.writeComment(
                        ownerId,
                        boardId,
                        taskIsd,
                        new UploadCommentRequest()
                ));
    }

    @Test
    @DisplayName("Upload Comment User Not Found Test")
    void uploadCommentUserNotFoundTest() {

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.writeComment(
                        UUID.randomUUID(),
                        boardId,
                        taskId,
                        new UploadCommentRequest()
                ));
    }

    @Test
    @DisplayName("Upload Comment User Not Assigned To Task")
    void uploadCommentUserNotAssignedToTask() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        member2.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member2.setCommentPermissions(Set.of(CommentPermission.COMMENT_TASK));
        boardRepository.save(board);

        assertThrows(RuntimeException.class,
                () -> taskActivityService.writeComment(
                        member2Id,
                        bId,
                        tId,
                        new UploadCommentRequest()
                ));
    }

    @Test
    @DisplayName("Upload Task User Not Board Member Test")
    void uploadTaskUSerNotBoardMemberTest() {

        UUID userId = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setUserId(userId);
        user.setUsername("Test User");
        user.setImageUrl("http://random123.com");
        userRepresentationRepository.save(user);

        BoardMember boardMember = new BoardMember(userId);
        boardMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardMember.setCommentPermissions(Set.of(CommentPermission.COMMENT_TASK));

        Board anotherBoard = new Board();
        anotherBoard.setTeamId(UUID.randomUUID());
        anotherBoard.setBoardName("Another Board");
        anotherBoard.setOwner(boardMember);
        boardRepository.save(anotherBoard);

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.writeComment(
                        userId,
                        bId,
                        tId,
                        new UploadCommentRequest()
                ));
    }

    @Test
    @DisplayName("Upload Comment User Without Permission Test")
    void uploadCommentUserWithoutPermissionTest() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        assertThrows(RuntimeException.class,
                () -> taskActivityService.writeComment(
                        member2Id,
                        bId,
                        tId,
                        new UploadCommentRequest()
                ));
    }
}
