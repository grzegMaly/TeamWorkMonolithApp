package com.mordiniaa.backend.mappers.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.mappers.task.activityMappers.TaskActivityMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskCategoryChangeDtoMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskCommentDtoMapper;
import com.mordiniaa.backend.mappers.task.activityMappers.dtoMappers.TaskStatusChangeDtoMapper;
import com.mordiniaa.backend.mappers.user.UserRepresentationMapper;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.TaskStatus;
import com.mordiniaa.backend.models.task.activity.TaskCategoryChange;
import com.mordiniaa.backend.models.task.activity.TaskComment;
import com.mordiniaa.backend.models.task.activity.TaskStatusChange;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TaskMapper.class,
        TaskActivityMapper.class,
        TaskCommentDtoMapper.class,
        TaskStatusChangeDtoMapper.class,
        TaskCategoryChangeDtoMapper.class,
        UserRepresentationMapper.class
})
public class TaskMapperToDtoTest {

    @Autowired
    private TaskMapper taskMapper;

    private final UUID user1Id = UUID.randomUUID();
    private final UUID user2Id = UUID.randomUUID();
    private final UUID user3Id = UUID.randomUUID();

    private UserRepresentation userRepresentation1;
    private final String user1Name = "User 1";
    private final String imageUrl1 = "https://random1.com";

    private UserRepresentation userRepresentation2;
    private final String user2Name = "User 2";
    private final String imageUrl2 = "https://random2.com";

    private UserRepresentation userRepresentation3;
    private final String user3Name = "User 3";
    private final String imageUrl3 = "https://random3.com";

    private Task task;

    private final ObjectId taskId = new ObjectId();
    private final String taskName = "Test Task";
    private final String description = "Task Description";
    private final int positionInCategory = 0;
    private final Instant createdAt = Instant.now().minus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant updatedAt = createdAt.plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant deadline = Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);

    private TaskComment taskComment1;
    private TaskComment taskComment2;
    private final String comment1 = "Comment 1";
    private final String comment2 = "Comment 2";

    private TaskStatusChange taskStatusChange1;
    private TaskStatusChange taskStatusChange2;
    private TaskStatus taskStatus1 = TaskStatus.COMPLETED;
    private TaskStatus taskStatus2 = TaskStatus.UNCOMPLETED;

    private TaskCategoryChange taskCategoryChange1;
    private TaskCategoryChange taskCategoryChange2;

    private TaskCategory taskCategory1;
    private TaskCategory taskCategory2;
    private TaskCategory taskCategory3;

    private final String taskCategoryName1 = "Started";
    private final String taskCategoryName2 = "In Progres";
    private final String taskCategoryName3 = "Done";

    private final Instant activityDate1 = createdAt.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate2 = activityDate1.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate3 = activityDate2.plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate4 = activityDate3.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate5 = activityDate4.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
    private final Instant activityDate6 = activityDate5.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);

    @BeforeEach
    void setup() {

        userRepresentation1 = new UserRepresentation();
        userRepresentation1.setUserId(user1Id);
        userRepresentation1.setUsername(user1Name);
        userRepresentation1.setImageUrl(imageUrl1);

        userRepresentation2 = new UserRepresentation();
        userRepresentation2.setUserId(user2Id);
        userRepresentation2.setUsername(user2Name);
        userRepresentation2.setImageUrl(imageUrl2);

        userRepresentation3 = new UserRepresentation();
        userRepresentation3.setUserId(user3Id);
        userRepresentation3.setUsername(user3Name);
        userRepresentation3.setImageUrl(imageUrl3);

        //--------------------------------------------------------------------

        taskCategory1 = new TaskCategory();
        taskCategory1.setCategoryName(taskCategoryName1);

        taskCategory2 = new TaskCategory();
        taskCategory2.setCategoryName(taskCategoryName2);

        taskCategory3 = new TaskCategory();
        taskCategory3.setCategoryName(taskCategoryName3);

        //-----------------------------TIMELINE---------------------------------------

        taskComment1 = new TaskComment();
        taskComment1.setUser(user1Id);
        taskComment1.setComment(comment1);
        taskComment1.setCreatedAt(activityDate1);

        taskCategoryChange1 = new TaskCategoryChange();
        taskCategoryChange1.setUser(user2Id);
        taskCategoryChange1.setCreatedAt(activityDate2);
        taskCategoryChange1.setPrevCategory(taskCategoryName1);
        taskCategoryChange1.setNextCategory(taskCategoryName2);

        taskComment2 = new TaskComment();
        taskComment2.setUser(user2Id);
        taskComment2.setComment(comment2);
        taskComment2.setCreatedAt(activityDate3);

        taskStatusChange1 = new TaskStatusChange();
        taskStatusChange1.setUser(user3Id);
        taskStatusChange1.setCreatedAt(activityDate4);
        taskStatusChange1.setPrevStatus(TaskStatus.UNCOMPLETED);
        taskStatusChange1.setNextStatus(taskStatus1);

        taskCategoryChange2 = new TaskCategoryChange();
        taskCategoryChange2.setUser(user1Id);
        taskCategoryChange2.setCreatedAt(activityDate5);
        taskCategoryChange2.setPrevCategory(taskCategoryName2);
        taskCategoryChange2.setPrevCategory(taskCategoryName3);

        taskStatusChange2 = new TaskStatusChange();
        taskStatusChange2.setUser(user3Id);
        taskStatusChange2.setCreatedAt(activityDate6);
        taskStatusChange2.setPrevStatus(taskStatus1);
        taskStatusChange2.setNextStatus(taskStatus2);

        task = new Task();
        task.setId(taskId);
        task.setTitle(taskName);
        task.setDescription(description);
        task.setPositionInCategory(positionInCategory);
        task.setCreatedAt(createdAt);
        task.setUpdatedAt(updatedAt);
        task.setDeadline(deadline);
        task.setCreatedBy(user1Id);
        task.setAssignedTo(Set.of(user1Id, user2Id, user3Id));
        task.setActivityElements(List.of(taskComment1, taskCategoryChange1, taskComment2, taskStatusChange1, taskCategoryChange2, taskStatusChange2));
    }

    @Test
    @DisplayName("Valid Map Test")
    void validMapTest() {

        TaskDetailsDTO detailsDTO = taskMapper.toDetailedDto(task, Map.of(
                user1Id, userRepresentation1,
                user2Id, userRepresentation2,
                user3Id, userRepresentation3));
        assertNotNull(detailsDTO);
        assertEquals(user1Id, detailsDTO.getCreatedBy());

        assertNotNull(detailsDTO.getAssignedTo());
        assertNotNull(detailsDTO.getTaskActivityElements());
        assertFalse(detailsDTO.getTaskActivityElements().isEmpty());

        assertEquals(activityDate6, detailsDTO.getTaskActivityElements().getFirst().getCreatedAt());
        assertNotNull(detailsDTO.getTaskActivityElements().getFirst().getUser());
        assertEquals(user3Id, detailsDTO.getTaskActivityElements().getFirst().getUser().getUserId());
        assertEquals(user3Name, detailsDTO.getTaskActivityElements().getFirst().getUser().getUsername());

        assertEquals(taskName, detailsDTO.getTitle());
        assertEquals(description, detailsDTO.getDescription());
        assertEquals(taskStatus2, detailsDTO.getTaskStatus());
    }
}
