package com.mordiniaa.backend.services.board;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.dto.user.mongodb.MongoUserDto;
import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
public class BUSGetBoardDetailsTest {

    @Autowired
    private BoardUserService boardUserService;

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepresentationRepository userRepository;

    private final UUID teamId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final UUID member1Id = UUID.randomUUID();
    private final UUID member2Id = UUID.randomUUID();

    private UserRepresentation owner;
    private UserRepresentation member1;
    private UserRepresentation member2;

    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;

    private Board board;

    @BeforeEach
    void setup() {

        owner = createUser(ownerId, "Owner Name", "https://random123.com");
        member1 = createUser(member1Id, "Member1 Name", "https://random321.com");
        member2 = createUser(member2Id, "Member2 Name", "https://random213.com");

        task1 = createTask(0, "Task1", "Description1", ownerId, Set.of(member1Id, member2Id), Instant.now().plus(3, ChronoUnit.DAYS));
        task2 = createTask(1, "Task2", "Description2", ownerId, Set.of(member1Id, member2Id), Instant.now().plus(3, ChronoUnit.DAYS));
        TaskCategory taskCategory1 = createTaskCategory(0, "Category1", Instant.now().truncatedTo(ChronoUnit.MILLIS));
        taskCategory1.setTasks(Set.of(task1.getId(), task2.getId()));

        task3 = createTask(0, "Task3", "Description3", ownerId, Set.of(member1Id, member2Id), Instant.now().plus(3, ChronoUnit.DAYS));
        task4 = createTask(1, "Task4", "Description4", ownerId, Set.of(member1Id, member2Id), Instant.now().plus(3, ChronoUnit.DAYS));
        TaskCategory taskCategory2 = createTaskCategory(1, "Category2", Instant.now().truncatedTo(ChronoUnit.MILLIS));
        taskCategory2.setTasks(Set.of(task3.getId(), task4.getId()));

        board = createBoard(createBoardMember(ownerId), teamId, "BoardName");
        board.setMembers(List.of(
                createBoardMember(member1Id),
                createBoardMember(member2Id)
        ));
        board.setTaskCategories(List.of(taskCategory1, taskCategory2));
        board.setNextPosition(2);
        board = boardRepository.save(board);
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Get Board Details Board Owner Valid Test")
    void getBoardDetailsValidTest() {

        BoardDetailsDto dto = boardUserService.getBoardDetails(ownerId, board.getId().toHexString(), teamId);
        assertNotNull(dto);

        MongoUserDto ownerDto = dto.getOwner();
        assertNotNull(ownerDto);
        assertEquals(ownerId, ownerDto.getUserId());
        assertEquals(owner.getUsername(), ownerDto.getUsername());
        assertEquals(owner.getImageUrl(), ownerDto.getImageUrl());
    }

    private BoardMember createBoardMember(UUID userId) {
        return new BoardMember(userId);
    }

    private Task createTask(int position, String title, String description, UUID createdBy, Set<UUID> assignedTo, Instant deadline) {
        Task task = new Task();
        task.setPositionInCategory(position);
        task.setTitle(title);
        task.setDescription(description);
        task.setCreatedBy(createdBy);
        task.setAssignedTo(assignedTo);
        task.setDeadline(deadline);
        return taskRepository.save(task);
    }

    private Board createBoard(BoardMember owner, UUID teamId, String boardName) {
        Board board = new Board();
        board.setBoardName(boardName);
        board.setOwner(owner);
        board.setTeamId(teamId);
        return board;
    }

    private UserRepresentation createUser(UUID userId, String username, String imageUrl) {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setImageUrl(imageUrl);
        user.setUserId(userId);
        return userRepository.save(user);
    }

    private TaskCategory createTaskCategory(int position, String categoryName, Instant createdAt) {

        TaskCategory taskCategory = new TaskCategory();
        taskCategory.setPosition(position);
        taskCategory.setCategoryName(categoryName);
        taskCategory.setCreatedAt(createdAt);

        return taskCategory;
    }
}
