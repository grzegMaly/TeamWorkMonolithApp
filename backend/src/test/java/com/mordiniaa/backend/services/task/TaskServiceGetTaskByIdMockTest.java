package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.activity.TaskActivityElementDto;
import com.mordiniaa.backend.dto.task.activity.TaskCategoryChangeDto;
import com.mordiniaa.backend.dto.task.activity.TaskCommentDto;
import com.mordiniaa.backend.dto.task.activity.TaskStatusChangeDto;
import com.mordiniaa.backend.dto.user.UserDto;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.TaskStatus;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import com.mordiniaa.backend.models.task.activity.TaskCategoryChange;
import com.mordiniaa.backend.models.task.activity.TaskComment;
import com.mordiniaa.backend.models.task.activity.TaskStatusChange;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceGetTaskByIdMockTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepresentationRepository userRepresentationRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private BoardAggregationRepository boardAggregationRepository;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private MongoIdUtils mongoIdUtils;

    private final UUID user1Id = UUID.randomUUID();
    private final UUID user2Id = UUID.randomUUID();
    private final UUID user3Id = UUID.randomUUID();

    private final String username1 = "User 1";
    private final String username2 = "User 2";
    private final String username3 = "User 3";

    private final String imageUrl1 = "https://random1.com";
    private final String imageUrl2 = "https://random2.com";
    private final String imageUrl3 = "https://random3.com";

    private final UserRepresentation user1 = getUser(user1Id, username1, imageUrl1);
    private final UserRepresentation user2 = getUser(user2Id, username2, imageUrl2);
    private final UserRepresentation user3 = getUser(user3Id, username3, imageUrl3);

    private final BoardMember boardMember1 = new BoardMember(user1Id);
    private final BoardMember boardMember2 = new BoardMember(user2Id);
    private final BoardMember boardMember3 = new BoardMember(user3Id);

    private final Instant createdAt = Instant.now().minus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant updatedAt = createdAt.plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant deadline = Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate1 = createdAt.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate2 = activityDate1.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate3 = activityDate2.plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate4 = activityDate3.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate5 = activityDate4.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate6 = activityDate5.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);

    private final TaskComment taskComment1 = getTaskComment(user1Id, "Comment 1", activityDate1);
    private final TaskCategoryChange taskCategoryChange1 = getTaskCategoryChange(user2Id, "Started", "In Progres", activityDate2);
    private final TaskComment taskComment2 = getTaskComment(user2Id, "Comment 2", activityDate3);
    private final TaskStatusChange taskStatusChange1 = getTaskStatusChange(user3Id, TaskStatus.UNCOMPLETED, TaskStatus.COMPLETED, activityDate4);
    private final TaskCategoryChange taskCategoryChange2 = getTaskCategoryChange(user1Id, "In Progres", "Done", activityDate5);
    private final TaskStatusChange taskStatusChange2 = getTaskStatusChange(user3Id, TaskStatus.COMPLETED, TaskStatus.UNCOMPLETED, activityDate6);

    private Task task;
    private Board board;

    @BeforeEach
    void setup() {

        task = new Task();
        task.setId(ObjectId.get());
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setCreatedBy(user1Id);
        task.setDeadline(deadline);
        task.setCreatedAt(createdAt);
        task.setUpdatedAt(updatedAt);
        task.setPositionInCategory(0);
        task.setActivityElements(List.of(taskComment1, taskCategoryChange1, taskComment2, taskStatusChange1, taskCategoryChange2, taskStatusChange2));

        setPermissions(boardMember1, Set.of(BoardPermission.values()), Set.of(CategoryPermissions.values()),
                Set.of(TaskPermission.values()), Set.of(CommentPermission.values()));
        setPermissions(boardMember2, Set.of(BoardPermission.VIEW_BOARD), Set.of(), Set.of(), Set.of());
        setPermissions(boardMember3, Set.of(BoardPermission.VIEW_BOARD), Set.of(), Set.of(), Set.of());

        board = new Board();
        board.setId(ObjectId.get());
        board.setOwner(boardMember1);
        board.setMembers(List.of(boardMember2, boardMember3));
    }

    @Test
    void getTaskById() {

        BoardMembersOnly boardProjection = mock(BoardMembersOnly.class);

        ObjectId boardObjectId = ObjectId.get();
        ObjectId taskId = task.getId();

        when(mongoIdUtils.getObjectId(anyString()))
                .thenReturn(boardObjectId, taskId);

        when(boardAggregationRepository
                .findBoardMembersForTask(
                        any(ObjectId.class),
                        any(UUID.class),
                        any(ObjectId.class)))
                .thenReturn(Optional.of(boardProjection));

        when(boardProjection.getMembers())
                .thenReturn(List.of(boardMember1, boardMember2, boardMember3));

        when(boardProjection.getOwner())
                .thenReturn(boardMember1);

        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.ofNullable(task));

        when(userRepresentationRepository.findAllByUserIdIn(Set.of(user1Id, user2Id, user3Id)))
                .thenReturn(List.of(user1, user2, user3));

        Map<UUID, UserRepresentation> users = Stream.of(user1, user2, user3).collect(Collectors.toMap(
                        UserRepresentation::getUserId,
                        Function.identity()
                ));

        when(taskMapper.toDetailedDto(task, users))
                .thenReturn(createDto(task, users));

        TaskDetailsDTO taskDetailsDTO = taskService.getTaskDetailsById(user1Id, ObjectId.get().toHexString(), task.getId().toHexString());
        assertNotNull(taskDetailsDTO);
    }

    private UserRepresentation getUser(UUID userId, String username, String imageUrl) {
        UserRepresentation user = new UserRepresentation();
        user.setUserId(userId);
        user.setUsername(username);
        user.setImageKey(imageUrl);
        return user;
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

    private void setPermissions(BoardMember member, Set<BoardPermission> bPerm,
                                Set<CategoryPermissions> catPerm, Set<TaskPermission> tPerm,
                                Set<CommentPermission> comPerm) {
        member.setBoardPermissions(bPerm);
        member.setCategoryPermissions(catPerm);
        member.setTaskPermissions(tPerm);
        member.setCommentPermissions(comPerm);
    }

    private TaskDetailsDTO createDto(Task task, Map<UUID, UserRepresentation> users) {

        TaskDetailsDTO dto = new TaskDetailsDTO();
        dto.setId(task.getId().toHexString());
        dto.setCreatedBy(task.getCreatedBy());
        dto.setDescription(task.getDescription());
        dto.setTitle(task.getTitle());
        dto.setDeadline(task.getDeadline());
        dto.setTaskStatus(task.getTaskStatus());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setPositionInCategory(task.getPositionInCategory());

        List<TaskActivityElementDto> elements = new ArrayList<>();
        for (TaskActivityElement element : task.getActivityElements()) {

            TaskActivityElementDto elementDto;

            UserRepresentation user = users.get(element.getUser());
            UserDto userDto = new UserDto();
            userDto.setUserId(user.getUserId());
            userDto.setUsername(user.getUsername());
            userDto.setImageUrl(user.getImageKey());

            switch (element) {
                case TaskComment tc -> {
                    TaskCommentDto adto = new TaskCommentDto();
                    adto.setComment(tc.getComment());
                    adto.setUpdated(tc.isUpdated());
                    elementDto = adto;
                }
                case TaskCategoryChange tcc -> {
                    TaskCategoryChangeDto adto = new TaskCategoryChangeDto();
                    adto.setPrevTaskCategoryName(tcc.getPrevCategory());
                    adto.setNextTaskCategoryName(tcc.getNextCategory());
                    elementDto = adto;
                }
                case TaskStatusChange tsc -> {
                    TaskStatusChangeDto adto = new TaskStatusChangeDto();
                    adto.setPrevStatus(tsc.getPrevStatus());
                    adto.setNextStatus(tsc.getNextStatus());
                    elementDto = adto;
                }
                default -> {
                    continue;
                }
            }

            elementDto.setCreatedAt(element.getCreatedAt());
            elementDto.setUser(userDto);

            elements.add(elementDto);
        }

        dto.setTaskActivityElements(elements);

        return dto;
    }
}
