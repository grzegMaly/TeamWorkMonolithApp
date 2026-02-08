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

    private String categoryName1 = "New Category1";
    private String categoryName2 = "New Category2";
    private String categoryName3 = "New Category3";
    private String categoryName4 = "New Category4";

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
        List<BoardDetailsDto.TaskCategoryDTO> categoryDTOS = dto.getTaskCategories();
        BoardDetailsDto.TaskCategoryDTO dtoName1 = categoryDTOS.stream()
                .filter(tc -> tc.getCategoryName().equals(categoryName1))
                .findFirst().orElseThrow();
        assertEquals(3, dtoName1.getPosition());

        BoardDetailsDto.TaskCategoryDTO dtoName2 = categoryDTOS.stream()
                .filter(tc -> tc.getCategoryName().equals(categoryName2))
                .findFirst().orElseThrow();
        assertEquals(0, dtoName2.getPosition());
    }
}
