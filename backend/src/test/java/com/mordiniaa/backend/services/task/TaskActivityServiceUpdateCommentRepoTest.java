package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.activity.TaskCommentDto;
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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
public class TaskActivityServiceUpdateCommentRepoTest {

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
    @Order(1)
    @DisplayName("Update Own Comment Owner Test")
    void updateOwnCommentOwnerTest() {

        String boardId = board.getId().toHexString();
        String taskId = task1.getId().toHexString();

        String originalComment = "Original Comment";
        TaskDetailsDTO dto = writeCommentAndResetPermission(ownerMember, taskId, originalComment);

        TaskCommentDto commentDto = (TaskCommentDto) dto.getTaskActivityElements().getFirst();
        assertEquals(originalComment, commentDto.getComment());
        UUID commentId = commentDto.getCommentId();

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        ownerMember.setCommentPermissions(Set.of(CommentPermission.EDIT_OWN_COMMENT));
        boardRepository.save(board);

        String updatedComment = "Updated Comment";
        UploadCommentRequest updateRequest = new UploadCommentRequest();
        updateRequest.setCommentId(commentId);
        updateRequest.setComment(updatedComment);

        TaskDetailsDTO updatedDto = taskActivityService.updateComment(
                ownerId,
                boardId,
                taskId,
                updateRequest
        );

        assertNotNull(updatedDto);
        TaskCommentDto updatedCommentDto = (TaskCommentDto) updatedDto.getTaskActivityElements().getFirst();
        assertEquals(updatedComment, updatedCommentDto.getComment());
        UUID sameId = updatedCommentDto.getCommentId();

        assertEquals(commentId, sameId);
        assertTrue(updatedCommentDto.isUpdated());
        assertEquals(updatedComment, updatedCommentDto.getComment());
    }

    @Test
    @Order(2)
    @DisplayName("Update Own Comment Member Test")
    void updateOwnCommentMemberTest() {

        String boardId = board.getId().toHexString();
        String taskId = task2.getId().toHexString();

        String originalComment = "Original Comment";
        TaskDetailsDTO dto = writeCommentAndResetPermission(member1, taskId, originalComment);

        TaskCommentDto commentDto = (TaskCommentDto) dto.getTaskActivityElements().getFirst();
        assertNotNull(commentDto);
        assertEquals(originalComment, commentDto.getComment());
        UUID commentId = commentDto.getCommentId();

        member1.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member1.setCommentPermissions(Set.of(CommentPermission.EDIT_OWN_COMMENT));
        boardRepository.save(board);

        String updatedComment = "Updated Comment";
        UploadCommentRequest updateRequest = new UploadCommentRequest();
        updateRequest.setCommentId(commentId);
        updateRequest.setComment(updatedComment);

        TaskDetailsDTO updatedDto = taskActivityService.updateComment(
                member1Id,
                boardId,
                taskId,
                updateRequest
        );

        assertNotNull(updatedDto);
        TaskCommentDto updatedCommentDto = (TaskCommentDto) updatedDto.getTaskActivityElements().getFirst();
        assertEquals(updatedComment, updatedCommentDto.getComment());
        UUID sameId = updatedCommentDto.getCommentId();

        assertEquals(commentId, sameId);
        assertTrue(updatedCommentDto.isUpdated());
        assertEquals(updatedComment, updatedCommentDto.getComment());
    }

    @Test
    @Order(3)
    @DisplayName("Update Own Comment Member Assigned Test")
    void updateOwnCommentMemberAssignedTest() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        task2.setAssignedTo(Set.of(member2Id));
        task2 = taskRepository.save(task2);

        String originalComment = "Original Comment";
        TaskDetailsDTO dto = writeCommentAndResetPermission(member2, tId, originalComment);

        TaskCommentDto commentDto = (TaskCommentDto) dto.getTaskActivityElements().getFirst();
        assertNotNull(commentDto);
        assertEquals(originalComment, commentDto.getComment());
        UUID commentId = commentDto.getCommentId();

        member2.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member2.setCommentPermissions(Set.of(CommentPermission.EDIT_OWN_COMMENT));
        boardRepository.save(board);

        String updatedComment = "Updated Comment";
        UploadCommentRequest updateRequest = new UploadCommentRequest();
        updateRequest.setCommentId(commentId);
        updateRequest.setComment(updatedComment);

        TaskDetailsDTO updatedDto = taskActivityService.updateComment(
                member2Id,
                bId,
                tId,
                updateRequest
        );

        assertNotNull(updatedDto);
        TaskCommentDto updatedCommentDto = (TaskCommentDto) updatedDto.getTaskActivityElements().getFirst();
        assertEquals(updatedComment, updatedCommentDto.getComment());
        UUID sameId = updatedCommentDto.getCommentId();

        assertEquals(commentId, sameId);
        assertTrue(updatedCommentDto.isUpdated());
        assertEquals(updatedComment, updatedCommentDto.getComment());
    }

    @Test
    @Order(4)
    @DisplayName("Update Comment CommentId Is Null Test")
    void updateCommentCommentIdIsNullTest() {

        assertThrows(RuntimeException.class,
                () -> taskActivityService.updateComment(ownerId, "", "", new UploadCommentRequest()));
    }

    @Test
    @Order(5)
    @DisplayName("Update Comment Already Updated Test")
    void updateCommentAlreadyUpdatedTest() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        TaskDetailsDTO dto = writeCommentAndResetPermission(ownerMember, tId, "Original Comment");
        assertNotNull(dto);

        assertInstanceOf(TaskCommentDto.class, dto.getTaskActivityElements().getFirst());
        TaskCommentDto commentDto = (TaskCommentDto) dto.getTaskActivityElements().getFirst();

        UUID commentId = commentDto.getCommentId();

        ownerMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        ownerMember.setCommentPermissions(Set.of(CommentPermission.EDIT_OWN_COMMENT));
        boardRepository.save(board);

        UploadCommentRequest updateRequest = new UploadCommentRequest();
        updateRequest.setCommentId(commentId);
        updateRequest.setComment("Comment");

        TaskDetailsDTO updatedDto = taskActivityService.updateComment(
                ownerId,
                bId,
                tId,
                updateRequest
        );

        assertNotNull(updatedDto);

        TaskCommentDto updatedCommentDto = (TaskCommentDto) updatedDto.getTaskActivityElements().getFirst();
        assertTrue(updatedCommentDto.isUpdated());

        UploadCommentRequest anotherUpdateCommentRequest = new UploadCommentRequest();
        anotherUpdateCommentRequest.setCommentId(commentId);

        assertThrows(RuntimeException.class,
                () -> taskActivityService.updateComment(
                        ownerId,
                        bId,
                        tId,
                        anotherUpdateCommentRequest
                ));

        Task task = taskService.findTaskById(task1.getId());
        TaskComment comment = (TaskComment) task.getActivityElements().getFirst();
        assertTrue(comment.isUpdated());
    }

    @Test
    @Order(6)
    @DisplayName("Update Not Owned Comment Throws Exception Test")
    void updateNotOwnedCommentThrowsExceptionTest() {

        String bId = board.getId().toHexString();
        String tId = task2.getId().toHexString();

        TaskDetailsDTO dto = writeCommentAndResetPermission(member1, tId, "Comment");

        UUID commentId = ((TaskCommentDto) dto.getTaskActivityElements().getFirst()).getCommentId();

        member2.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        member2.setCommentPermissions(Set.of(CommentPermission.EDIT_OWN_COMMENT));
        boardRepository.save(board);

        UploadCommentRequest commentRequest = new UploadCommentRequest();
        commentRequest.setComment("Comment");
        commentRequest.setCommentId(commentId);

        assertThrows(RuntimeException.class,
                () -> taskActivityService.updateComment(
                        member2Id,
                        bId,
                        tId,
                        commentRequest
                ));
    }

    @Test
    @Order(7)
    @DisplayName("Update Comment Member Not Permitted Throws Exception Test")
    void updateCommentMemberNotPermittedThrowsExceptionTest() {

        String bId = board.getId().toHexString();
        String tId = task1.getId().toHexString();

        TaskDetailsDTO dto = writeCommentAndResetPermission(ownerMember, tId, "Comment");
        UUID commentId = ((TaskCommentDto) dto.getTaskActivityElements().getFirst()).getCommentId();

        UploadCommentRequest updateRequest = new UploadCommentRequest();
        updateRequest.setComment("Updated Comment");
        updateRequest.setCommentId(commentId);

        assertThrows(RuntimeException.class,
                () -> taskActivityService.updateComment(member1Id, bId, tId, updateRequest));
    }

    private TaskDetailsDTO writeCommentAndResetPermission(BoardMember boardMember, String taskId, String comment) {

        boardMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        boardMember.setCommentPermissions(Set.of(CommentPermission.COMMENT_TASK));
        boardRepository.save(board);

        UploadCommentRequest request = new UploadCommentRequest();
        request.setComment(comment);
        TaskDetailsDTO dto = taskActivityService.writeComment(
                boardMember.getUserId(),
                board.getId().toHexString(),
                taskId,
                request
        );

        boardMember.setBoardPermissions(Set.of());
        boardMember.setCommentPermissions(Set.of());
        boardRepository.save(board);

        return dto;
    }
}
