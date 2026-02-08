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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class DOTCSReorderTaskCategoriesTest {

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

    private final String categoryName1 = "New Category1";
    private final String categoryName2 = "New Category2";
    private final String categoryName3 = "New Category3";
    private final String categoryName4 = "New Category4";

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

        for (String categoryName : List.of(categoryName1, categoryName2, categoryName3, categoryName4)) {
            TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
            taskCategoryRequest.setNewCategoryName(categoryName);
            boardOwnerTaskCategoryService.createTaskCategory(owner.getUserId(), board.getId().toHexString(), taskCategoryRequest);
        }
    }

    @AfterEach
    void clear() {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        userRepresentationRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("Move Category Up Valid Test")
    void moveCategoryUpValidTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName1);

        BoardDetailsDto dto = boardOwnerTaskCategoryService.reorderTaskCategories(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                taskCategoryRequest,
                3
        );

        assertNotNull(dto);

        assertFalse(dto.getTaskCategories().isEmpty());

        BoardDetailsDto.TaskCategoryDTO dtoName1 = getTaskCategory(dto, categoryName1);
        assertEquals(3, dtoName1.getPosition());

        BoardDetailsDto.TaskCategoryDTO dtoName2 = getTaskCategory(dto, categoryName2);
        assertEquals(0, dtoName2.getPosition());
    }

    @Test
    @DisplayName("Move Category Down Valid Test")
    void moveCategoryDownValidTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName4);

        BoardDetailsDto dto = boardOwnerTaskCategoryService.reorderTaskCategories(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                taskCategoryRequest,
                0
        );

        assertNotNull(dto);

        assertFalse(dto.getTaskCategories().isEmpty());

        BoardDetailsDto.TaskCategoryDTO dtoName4 = getTaskCategory(dto, categoryName4);
        assertEquals(0, dtoName4.getPosition());

        BoardDetailsDto.TaskCategoryDTO dtoName3 = getTaskCategory(dto, categoryName3);
        assertEquals(3, dtoName3.getPosition());

        BoardDetailsDto.TaskCategoryDTO dtoName2 = getTaskCategory(dto, categoryName2);
        assertEquals(2, dtoName2.getPosition());
    }

    @Test
    @DisplayName("From Middle Up Test")
    void fromMiddleUpTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName2);

        BoardDetailsDto dto = boardOwnerTaskCategoryService.reorderTaskCategories(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                taskCategoryRequest,
                3
        );

        assertNotNull(dto);

        BoardDetailsDto.TaskCategoryDTO category2Dto = getTaskCategory(dto, categoryName2);
        assertEquals(3, category2Dto.getPosition());

        BoardDetailsDto.TaskCategoryDTO category1Dto = getTaskCategory(dto, categoryName1);
        assertEquals(0, category1Dto.getPosition());

        BoardDetailsDto.TaskCategoryDTO category3Dto = getTaskCategory(dto, categoryName3);
        assertEquals(1, category3Dto.getPosition());

        BoardDetailsDto.TaskCategoryDTO category4Dto = getTaskCategory(dto, categoryName4);
        assertEquals(2, category4Dto.getPosition());
    }

    @Test
    @DisplayName("Switch Up Test")
    void switchUpTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName2);

        BoardDetailsDto dto = boardOwnerTaskCategoryService.reorderTaskCategories(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                taskCategoryRequest,
                2
        );

        assertNotNull(dto);

        BoardDetailsDto.TaskCategoryDTO taskCategoryDTO2 = getTaskCategory(dto, categoryName2);
        assertEquals(2, taskCategoryDTO2.getPosition());

        BoardDetailsDto.TaskCategoryDTO taskCategoryDTO3 = getTaskCategory(dto, categoryName3);
        assertEquals(1, taskCategoryDTO3.getPosition());
    }

    @Test
    @DisplayName("Switch Down Test")
    void switchDownTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName3);

        BoardDetailsDto dto = boardOwnerTaskCategoryService.reorderTaskCategories(
                owner.getUserId(),
                board.getId().toHexString(),
                team.getTeamId(),
                taskCategoryRequest,
                1
        );

        assertNotNull(dto);

        BoardDetailsDto.TaskCategoryDTO taskCategoryDTO3 = getTaskCategory(dto, categoryName3);
        assertEquals(1, taskCategoryDTO3.getPosition());

        BoardDetailsDto.TaskCategoryDTO taskCategoryDTO2 = getTaskCategory(dto, categoryName2);
        assertEquals(2, taskCategoryDTO2.getPosition());
    }

    @Test
    @DisplayName("Same Position Invalid Test")
    void samePositionInvalidTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName2);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.reorderTaskCategories(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest,
                        1
                ));
    }

    @Test
    @DisplayName("Position Out Of Bounds")
    void positionOutOfBounds() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName(categoryName2);

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.reorderTaskCategories(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest,
                        999
                ));

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.reorderTaskCategories(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest,
                        4
                ));

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.reorderTaskCategories(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest,
                        -1
                ));
    }

    @Test
    @DisplayName("Rename Task Category Category Empty Test")
    void renameTaskCategoryCategoryEmptyTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName("      ");

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.reorderTaskCategories(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest,
                        2
                ));
    }

    @Test
    @DisplayName("Rename Task Category Category Not Found Test")
    void renameTaskCategoryCategoryNotFoundTest() {

        TaskCategoryRequest taskCategoryRequest = new TaskCategoryRequest();
        taskCategoryRequest.setExistingCategoryName("Not Found Category");

        assertThrows(RuntimeException.class,
                () -> boardOwnerTaskCategoryService.reorderTaskCategories(
                        owner.getUserId(),
                        board.getId().toHexString(),
                        team.getTeamId(),
                        taskCategoryRequest,
                        2
                ));
    }

    private BoardDetailsDto.TaskCategoryDTO getTaskCategory(BoardDetailsDto dto, String categoryName) {
        return dto.getTaskCategories().stream()
                .filter(tc -> tc.getCategoryName().equals(categoryName))
                .findFirst().orElseThrow();
    }
}
