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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class BOTCSRenameTaskCategoryRepoTest {

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

    private String categoryName = "New Category";

    private UserRepresentation userRepresentation;

    @BeforeEach
    void setup() {

        Role role = new Role(AppRole.ROLE_MANAGER);
        roleRepository.save(role);

        User user = new User();
        user.setLastName("LastName");
        user.setFirstName("FirstName");
        user.setEmail("email@gmail.com");
        user.setUsername("Username");
        user.setPassword("SecretPassword");
        user.setRole(roleRepository.getReferenceById(1));
        user = userRepository.save(user);

        userRepresentation = new UserRepresentation();
        userRepresentation.setUserId(user.getUserId());
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setImageUrl("IMAGE");
        userRepresentationRepository.save(userRepresentation);

        team = new Team();
        team.setManager(user);
        team.setTeamName("Name");
        team = teamRepository.save(team);

        owner = new BoardMember(user.getUserId());

        board = new Board();
        board.setBoardName("BoardName");
        board.setOwner(owner);
        board.setTeamId(team.getTeamId());
        board = boardRepository.save(board);

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName(categoryName);
        boardOwnerTaskCategoryService.createTaskCategory(owner.getUserId(), board.getId().toHexString(), taskCategoryRequest);
    }

    @AfterEach
    void clear() {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        userRepresentationRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Rename Task Category Valid Test")
    void renameTaskCategoryValidTest() {

        TaskCategoryRequest renameRequest = new TaskCategoryRequest();
        renameRequest.setNewCategoryName("New Name");
        renameRequest.setExistingCategoryName(categoryName);

        BoardDetailsDto boardDetailsDto = boardOwnerTaskCategoryService.renameTaskCategory(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                renameRequest
        );

        assertNotNull(boardDetailsDto);
        assertEquals(1, boardDetailsDto.getTaskCategories().size());
        assertEquals(renameRequest.getNewCategoryName(), boardDetailsDto.getTaskCategories().getFirst().getCategoryName());
    }

    @Test
    @Order(2)
    @DisplayName("Rename Task Category Same Name Test")
    void renameTaskCategorySameNameTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName(categoryName);
        taskCategoryRequest.setExistingCategoryName(categoryName);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.renameTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @Order(3)
    @DisplayName("Rename Category Name Existing Category Empty Test")
    void renameCategoryNameNewCategoryEmptyTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName("New Name");
        taskCategoryRequest.setExistingCategoryName("    ");
        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.renameTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @Order(4)
    @DisplayName("Rename Category Name User Not Active Test")
    void renameCategoryNameUserNotActiveTest() {

        userRepresentation.setDeleted(true);
        userRepresentationRepository.save(userRepresentation);

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName("New Name");
        taskCategoryRequest.setExistingCategoryName(categoryName);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.renameTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @Order(5)
    @DisplayName("Rename Task Category User Not Board Member Test")
    void renameTaskCategoryUserNotBoardMemberTest() {

        UserRepresentation newUser = new UserRepresentation();
        userRepresentation.setUsername("New Name");
        userRepresentation.setUserId(UUID.randomUUID());
        userRepresentation.setImageUrl("IMAGE");
        userRepresentationRepository.save(newUser);

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName("New Name");
        taskCategoryRequest.setExistingCategoryName(categoryName);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.renameTaskCategory(
                        newUser.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest
                ));
    }

    @Test
    @Order(6)
    @DisplayName("Rename Task Category Team Not Found Test")
    void renameTaskCategoryTeamNotFoundTest() {
        UUID teamId = UUID.randomUUID();

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setNewCategoryName("New Name");
        taskCategoryRequest.setExistingCategoryName(categoryName);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.renameTaskCategory(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        teamId,
                        taskCategoryRequest
                ));
    }
}
