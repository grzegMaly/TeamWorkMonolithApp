package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.team.Team;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.models.user.mysql.AppRole;
import com.mordiniaa.backend.models.user.mysql.Role;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mysql.RoleRepository;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.request.board.TaskCategoryRequest;
import com.mordiniaa.backend.request.task.CreateTaskRequest;
import com.mordiniaa.backend.services.task.TaskService;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BOTCSDeleteTaskCategoryTest {

    @Autowired
    private BoardOwnerTaskCategoryService boardOwnerTaskCategoryService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepresentationRepository userRepresentationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TaskRepository taskRepository;

    private BoardMember owner;

    private Board board;

    private Team team;

    private String categoryName1 = "New Category1";
    private String categoryName2 = "New Category2";

    private UserRepresentation userRepresentation;
    @Autowired
    private TaskService taskService;

    @BeforeEach
    void setup() {

        Role role = roleRepository.findRoleByAppRole(AppRole.ROLE_MANAGER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_MANAGER)));

        User user = new User();
        user.setLastName("LastName");
        user.setFirstName("FirstName");
        user.setUsername("Username");
        user.setImageKey("KEY");
        user.setPassword("SecretPassword");
        user.setRole(role);
        user = userRepository.save(user);

        userRepresentation = new UserRepresentation();
        userRepresentation.setUserId(user.getUserId());
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setImageKey("IMAGE");
        userRepresentationRepository.save(userRepresentation);

        team = new Team();
        team.setManager(user);
        team.setTeamName("Name");
        team.setPresentationName("Name");
        team = teamRepository.save(team);

        owner = new BoardMember(user.getUserId());
        owner.setBoardPermissions(Set.of(BoardPermission.values()));
        owner.setTaskPermissions(Set.of(TaskPermission.values()));
        owner.setCategoryPermissions(Set.of(CategoryPermissions.values()));
        owner.setCommentPermissions(Set.of(CommentPermission.values()));

        board = new Board();
        board.setBoardName("BoardName");
        board.setOwner(owner);
        board.setTeamId(team.getTeamId());
        board = boardRepository.save(board);

        TaskCategoryRequest taskCategoryRequest1 = new TaskCategoryRequest();
        taskCategoryRequest1.setNewCategoryName(categoryName1);
        boardOwnerTaskCategoryService.createTaskCategory(owner.getUserId(), board.getId().toHexString(), taskCategoryRequest1);

        TaskCategoryRequest taskCategoryRequest2 = new TaskCategoryRequest();
        taskCategoryRequest2.setNewCategoryName(categoryName2);
        boardOwnerTaskCategoryService.createTaskCategory(owner.getUserId(), board.getId().toHexString(), taskCategoryRequest2);

        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("Title1");
        request1.setDescription("Description1");
        request1.setDeadline(Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));

        taskService.createTask(
                owner.getUserId(),
                board.getId().toHexString(),
                categoryName1,
                request1
        );

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("Title2");
        request2.setDescription("Description2");
        request2.setDeadline(Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));

        taskService.createTask(
                owner.getUserId(),
                board.getId().toHexString(),
                categoryName1,
                request2
        );

        CreateTaskRequest request3 = new CreateTaskRequest();
        request3.setTitle("Title3");
        request3.setDescription("Description3");
        request3.setDeadline(Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));

        taskService.createTask(
                owner.getUserId(),
                board.getId().toHexString(),
                categoryName2,
                request3
        );

        CreateTaskRequest request4 = new CreateTaskRequest();
        request4.setTitle("Title4");
        request4.setDescription("Description4");
        request4.setDeadline(Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));

        taskService.createTask(
                owner.getUserId(),
                board.getId().toHexString(),
                categoryName2,
                request4
        );
    }

    @AfterEach
    void clear() {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        userRepresentationRepository.deleteAll();
        boardRepository.deleteAll();
        taskRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Delete Task Category Valid Test")
    void deleteTaskCategoryValidTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName1);

        BoardDetailsDto dto = boardOwnerTaskCategoryService.deleteTaskCategory(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                taskCategoryRequest
        );

        assertNotNull(dto);
        assertEquals(1, dto.getTaskCategories().size());

        BoardDetailsDto.TaskCategoryDTO categoryDTO = dto.getTaskCategories().getFirst();
        assertEquals(0, categoryDTO.getPosition());

        Set<String> taskNames = dto.getTaskCategories().getFirst().getTasks()
                .stream().map(TaskShortDto::getTitle).collect(Collectors.toSet());
        assertTrue(taskNames.containsAll(Set.of("Title3", "Title4")));

        List<Task> tasks = taskRepository.findAll();
        assertEquals(2, tasks.size());

        board = boardRepository.findById(board.getId())
                .orElseThrow();

        assertEquals(1, board.getNextPosition());
    }

    @Test
    @DisplayName("Delete Task Category User Not Active Test")
    void deleteTaskCategoryUserNotActiveTest() {

        userRepresentation.setDeleted(true);
        userRepresentationRepository.save(userRepresentation);

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName1);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.deleteTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @DisplayName("Delete Task Category Existing Category Empty")
    void deleteTaskCategoryExistingCategoryEmpty() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName("      ");

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.deleteTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @DisplayName("Delete Task Category User Not Board Owner Test")
    void deleteTaskCategoryUserNotBoardOwnerTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName2);

        UserRepresentation user = new UserRepresentation();
        user.setUserId(UUID.randomUUID());
        user.setImageKey("IMAGE");
        user.setUsername("User");
        userRepresentationRepository.save(user);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.deleteTaskCategory(
                        user.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @DisplayName("Delete Task Category Not Found Test")
    void deleteTaskCategoryNotFoundTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName("New Name");

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.deleteTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @DisplayName("Delete Task Category Not Containing Tasks Test")
    void deleteTaskCategoryNotContainingTasksTest() {

        String taskName = "New Task Name";
        TaskCategoryRequest creationRequest = new TaskCategoryRequest();
        creationRequest.setNewCategoryName(taskName);

        boardOwnerTaskCategoryService.createTaskCategory(
                owner.getUserId(),
                board.getId().toHexString(),
                creationRequest
        );

        TaskCategoryRequest deletionRequest = new TaskCategoryRequest();
        deletionRequest.setExistingCategoryName(taskName);

        BoardDetailsDto dto = boardOwnerTaskCategoryService.deleteTaskCategory(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                deletionRequest
        );

        Set<String> names = dto.getTaskCategories().stream()
                .map(BoardDetailsDto.TaskCategoryDTO::getCategoryName)
                .collect(Collectors.toSet());

        assertFalse(names.contains(taskName));
    }
}
