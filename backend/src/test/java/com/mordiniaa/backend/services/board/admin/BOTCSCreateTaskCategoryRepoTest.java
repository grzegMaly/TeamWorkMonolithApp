package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.team.Team;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.models.user.mysql.AppRole;
import com.mordiniaa.backend.models.user.mysql.Role;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mysql.RoleRepository;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.request.board.TaskCategoryRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BOTCSCreateTaskCategoryRepoTest {

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

    private BoardMember owner;

    private Board board;

    private Team team;

    private UserRepresentation userRepresentation;

    @BeforeEach
    void setup() {

        Role role = roleRepository.findRoleByAppRole(AppRole.ROLE_MANAGER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_MANAGER)));

        User user = new User();
        user.setLastName("LastName");
        user.setFirstName("FirstName");
        user.setUsername("Username");
        user.setPassword("SecretPassword");
        user.setImageKey("KEY");
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

        board = new Board();
        board.setBoardName("BoardName");
        board.setOwner(owner);
        board.setTeamId(team.getTeamId());
        board = boardRepository.save(board);
    }

    @AfterEach
    void clear() {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        userRepresentationRepository.deleteAll();
        boardRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Create Task Category Valid Test")
    void createTaskCategoryValidTest() {

        TaskCategoryRequest taskCategoryRequest1 = new TaskCategoryRequest();
        taskCategoryRequest1.setNewCategoryName("New Category1");

        BoardDetailsDto boardDetailsDto1 = boardOwnerTaskCategoryService.createTaskCategory(
                owner.getUserId(),
                board.getId().toHexString(),
                taskCategoryRequest1
        );

        assertNotNull(boardDetailsDto1);
        List<BoardDetailsDto.TaskCategoryDTO> categories = boardDetailsDto1.getTaskCategories();
        assertFalse(categories.isEmpty());
        assertEquals(1, categories.size());

        BoardDetailsDto.TaskCategoryDTO categoryDTO1 = categories.stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategoryRequest1.getNewCategoryName()))
                .findFirst().orElseThrow();
        assertEquals(0, categoryDTO1.getPosition());

        TaskCategoryRequest taskCategoryRequest2 = new TaskCategoryRequest();
        taskCategoryRequest2.setNewCategoryName("New Category2");
        BoardDetailsDto boardDetailsDto2 = boardOwnerTaskCategoryService.createTaskCategory(
                owner.getUserId(),
                board.getId().toHexString(),
                taskCategoryRequest2
        );

        assertNotNull(boardDetailsDto2);
        categories = boardDetailsDto2.getTaskCategories();
        assertEquals(2, categories.size());

        BoardDetailsDto.TaskCategoryDTO categoryDTO11 = categories.stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategoryRequest1.getNewCategoryName()))
                .findFirst().orElseThrow();
        assertEquals(0, categoryDTO11.getPosition());

        BoardDetailsDto.TaskCategoryDTO categoryDTO2 = categories.stream()
                .filter(tc -> tc.getCategoryName().equals(taskCategoryRequest2.getNewCategoryName()))
                .findFirst().orElseThrow();
        assertEquals(1, categoryDTO2.getPosition());

        board = boardRepository.findById(board.getId())
                .orElseThrow();
        assertEquals(2, board.getNextPosition());
    }

    @Test
    @Order(2)
    @DisplayName("Create Task Category Category Already Exists Test")
    void createTaskCategoryCategoryAlreadyExistsTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName("New Category");

        BoardDetailsDto boardDetailsDto = boardOwnerTaskCategoryService.createTaskCategory(
                owner.getUserId(),
                board.getId().toHexString(),
                taskCategoryRequest
        );

        assertNotNull(boardDetailsDto);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.createTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        taskCategoryRequest
                ));

        board = boardRepository.findById(board.getId())
                .orElseThrow();

        assertEquals(1, board.getNextPosition());
        assertEquals(1, board.getTaskCategories().size());
    }

    @Test
    @Order(3)
    @DisplayName("Create Task Category User Not Active Test")
    void createTaskCategoryUserNotActiveTest() {

        userRepresentation.setDeleted(true);
        userRepresentationRepository.save(userRepresentation);

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName("New Category");
        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.createTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        taskCategoryRequest
                ));
    }

    @Test
    @Order(4)
    @DisplayName("Create Task Category User Not Board Owner Test")
    void createTaskCategoryUserNotBoardOwnerTest() {

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUserId(UUID.randomUUID());
        newUser.setImageKey("IMAGE");
        newUser.setUsername("Username2");
        userRepresentationRepository.save(newUser);

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName("New Category");

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.createTaskCategory(
                        newUser.getUserId(),
                        board.getId().toHexString(),
                        taskCategoryRequest
                ));
    }
}
