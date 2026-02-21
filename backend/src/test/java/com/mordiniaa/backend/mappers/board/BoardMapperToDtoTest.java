package com.mordiniaa.backend.mappers.board;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.TaskStatus;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BoardMapperToDtoTest {

    @Autowired
    private BoardMapper boardMapper;

    private final UUID teamId = UUID.randomUUID();

    private final ObjectId ownerOId = ObjectId.get();
    private final ObjectId member1OId = ObjectId.get();
    private final ObjectId member2OId = ObjectId.get();
    private final ObjectId member3OId = ObjectId.get();

    private final ObjectId task1OId = ObjectId.get();
    private final ObjectId task2OId = ObjectId.get();
    private final ObjectId task3OId = ObjectId.get();
    private final ObjectId task4OId = ObjectId.get();

    private final ObjectId boardOId = ObjectId.get();

    private final UUID ownerId = UUID.randomUUID();
    private final UUID member1Id = UUID.randomUUID();
    private final UUID member2Id = UUID.randomUUID();
    private final UUID member3Id = UUID.randomUUID();

    private final String imageUrl = "https://random123.com";

    private final String name1 = "Name1";
    private final String name2 = "Name2";
    private final String name3 = "Name3";
    private final String name4 = "Name4";

    private final String boardName = "BoardName";

    private final String task1Name = "Task1";
    private final String task2Name = "Task2";
    private final String task3Name = "Task3";
    private final String task4Name = "Task4";

    private final String task1Description = "Description1";
    private final String task2Description = "Description2";
    private final String task3Description = "Description3";
    private final String task4Description = "Description4";

    private final Instant deadline1 = Instant.now().plus(1, ChronoUnit.DAYS);
    private final Instant deadline2 = Instant.now().plus(2, ChronoUnit.DAYS);
    private final Instant deadline3 = Instant.now().plus(3, ChronoUnit.DAYS);
    private final Instant deadline4 = Instant.now().plus(4, ChronoUnit.DAYS);

    private String taskCategoryName1 = "TaskCategoryName1";
    private String taskCategoryName2 = "TaskCategoryName2";

    private BoardFull board;
    private BoardFull.TaskCategoryFull taskCategory1;
    private BoardFull.TaskCategoryFull taskCategory2;

    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;

    private UserRepresentation owner;
    private UserRepresentation member1;
    private UserRepresentation member2;
    private UserRepresentation member3;

    @BeforeEach
    void setup() {

        owner = new UserRepresentation();
        owner.setId(ownerOId);
        owner.setUsername(name1);
        owner.setUserId(ownerId);
        owner.setImageKey(imageUrl);

        member1 = new UserRepresentation();
        member1.setId(member1OId);
        member1.setUsername(name2);
        member1.setUserId(member1Id);
        member1.setImageKey(imageUrl);

        member2 = new UserRepresentation();
        member2.setId(member2OId);
        member2.setUsername(name3);
        member2.setUserId(member2Id);
        member2.setImageKey(imageUrl);

        member3 = new UserRepresentation();
        member3.setId(member3OId);
        member3.setUsername(name4);
        member3.setUserId(member3Id);
        member3.setImageKey(imageUrl);

        taskCategory1 = new BoardFull.TaskCategoryFull();
        taskCategory1.setCategoryName(taskCategoryName1);
        taskCategory1.setPosition(0);
        taskCategory1.setCreatedAt(Instant.now().minus(2, ChronoUnit.DAYS));

        taskCategory2 = new BoardFull.TaskCategoryFull();
        taskCategory2.setCategoryName(taskCategoryName2);
        taskCategory2.setPosition(1);
        taskCategory2.setCreatedAt(Instant.now().minus(2, ChronoUnit.DAYS));

        task1 = new Task();
        task1.setId(task1OId);
        task1.setTitle(task1Name);
        task1.setDeadline(deadline1);
        task1.setDescription(task1Description);
        task1.setCreatedBy(member1Id);
        task1.setAssignedTo(Set.of(member2Id, member3Id));
        task1.setPositionInCategory(0);
        task1.setTaskStatus(TaskStatus.COMPLETED);

        task2 = new Task();
        task2.setId(task2OId);
        task2.setTitle(task2Name);
        task2.setDeadline(deadline2);
        task2.setDescription(task2Description);
        task2.setCreatedBy(ownerId);
        task2.setAssignedTo(Set.of(member1Id, member2Id));
        task2.setPositionInCategory(1);
        task2.setTaskStatus(TaskStatus.COMPLETED);

        task3 = new Task();
        task3.setId(task3OId);
        task3.setTitle(task3Name);
        task3.setDeadline(deadline3);
        task3.setDescription(task3Description);
        task3.setCreatedBy(member2Id);
        task3.setAssignedTo(Set.of(member1Id, member2Id));
        task3.setPositionInCategory(0);
        task3.setTaskStatus(TaskStatus.UNCOMPLETED);

        task4 = new Task();
        task4.setId(task4OId);
        task4.setTitle(task4Name);
        task4.setDeadline(deadline4);
        task4.setDescription(task4Description);
        task4.setCreatedBy(member3Id);
        task4.setAssignedTo(Set.of(member2Id, member3Id));
        task4.setPositionInCategory(1);
        task4.setTaskStatus(TaskStatus.UNCOMPLETED);

        taskCategory1.setTasks(Set.of(task1, task2));
        taskCategory2.setTasks(Set.of(task3, task4));

        board = new BoardFull();
        board.setId(boardOId);
        board.setBoardName(boardName);
        board.setTeamId(teamId);
        board.setOwner(owner);
        board.setMembers(List.of(member1, member2, member3));
        board.setTaskCategories(List.of(taskCategory1, taskCategory2));
    }

    @Test
    void toDtoTest() {

        BoardDetailsDto dto = boardMapper.toDetailedDto(board);
        assertNotNull(dto);
        assertEquals(boardName, dto.getBoardName());
        assertEquals(boardOId.toHexString(), dto.getBoardId());

        assertFalse(dto.getMembers().isEmpty());
        assertEquals(ownerId, dto.getOwner().getUserId());

        assertFalse(dto.getTaskCategories().isEmpty());
        List<BoardDetailsDto.TaskCategoryDTO> taskCategories = dto.getTaskCategories();
        assertEquals(0, taskCategories.getFirst().getPosition());
        assertEquals(1, taskCategories.getLast().getPosition());

        TaskShortDto shortTask = taskCategories.stream().filter(tC -> tC.getCategoryName().equals(taskCategoryName1))
                .findFirst()
                .orElseThrow()
                .getTasks().stream()
                .filter(t -> t.getId().equals(task1OId.toHexString()))
                .findFirst().orElseThrow();

        assertNotNull(shortTask);
        assertEquals(task1OId.toHexString(), shortTask.getId());
        assertEquals(0, shortTask.getPositionInCategory());
        assertEquals(task1Name, shortTask.getTitle());
        assertEquals(TaskStatus.COMPLETED, shortTask.getTaskStatus());
        assertEquals(deadline1, shortTask.getDeadline());
        assertEquals(member1Id, shortTask.getCreatedBy());
        assertTrue(shortTask.getAssignedTo().containsAll(Set.of(member2Id, member3Id)));
    }
}
