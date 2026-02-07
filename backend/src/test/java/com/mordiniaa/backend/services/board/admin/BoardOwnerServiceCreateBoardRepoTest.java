package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
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
import com.mordiniaa.backend.request.board.BoardCreationRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class BoardOwnerServiceCreateBoardRepoTest {

    @Autowired
    private BoardOwnerService boardOwnerService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepresentationRepository userRepresentationRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserRepresentation ownerUser;

    private Team team;

    @BeforeEach
    void setup() {

        Role managerRole = new Role(AppRole.ROLE_MANAGER);
        managerRole = roleRepository.save(managerRole);

        User user = new User();
        user.setUsername("Username");
        user.setRole(managerRole);
        user.setEmail("email@gmail.com");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setPassword("SuperSecretPassword");
        user = userRepository.save(user);

        ownerUser = new UserRepresentation();
        ownerUser.setUserId(user.getUserId());
        ownerUser.setUsername("Username");
        ownerUser.setImageUrl("ImageURL");
        ownerUser = userRepresentationRepository.save(ownerUser);

        team = new Team();
        team.setTeamName("Test Team");
        team.setManager(user);
        team = teamRepository.save(team);
    }

    @AfterEach
    void clear() {
        boardRepository.deleteAll();
        userRepresentationRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create Board Valid Test")
    void createBoardValidTest() {

        BoardCreationRequest boardCreationRequest = new BoardCreationRequest();
        boardCreationRequest.setBoardName("BoardName");
        boardCreationRequest.setTeamId(team.getTeamId());

        BoardDetailsDto boardDetailsDto = boardOwnerService.createBoard(ownerUser.getUserId(), boardCreationRequest);
        assertNotNull(boardDetailsDto);

        assertEquals(boardCreationRequest.getBoardName(), boardDetailsDto.getBoardName());
        assertEquals(ownerUser.getUserId(), boardDetailsDto.getOwner().getUserId());
    }

    @Test
    @DisplayName("Create Board User Inactive Test")
    void createBoardUserInactiveTest() {

        ownerUser.setDeleted(true);
        userRepresentationRepository.save(ownerUser);
        assertThrows(RuntimeException.class,
                () -> boardOwnerService.createBoard(ownerUser.getUserId(), new BoardCreationRequest()));
    }

    @Test
    @DisplayName("Create Board User Not Board Manager Test")
    void createBoardUserNotBoardManagerTest() {

        BoardCreationRequest boardCreationRequest = new BoardCreationRequest();
        boardCreationRequest.setBoardName("BoardName");
        boardCreationRequest.setTeamId(team.getTeamId());

        User newUser = new User();
        newUser.setPassword("SuperSecret");
        newUser.setRole(roleRepository.getReferenceById(1));
        newUser.setUsername("Username");
        newUser.setEmail("email@gmail.com");
        newUser.setFirstName("FirstName");
        newUser.setLastName("LastName");
        userRepository.save(newUser);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUserId(newUser.getUserId());
        userRepresentation.setImageUrl("ImageURL");
        userRepresentation.setUsername(newUser.getUsername());
        userRepresentationRepository.save(userRepresentation);

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.createBoard(newUser.getUserId(), boardCreationRequest));
    }

    @Test
    @DisplayName("Create Board Team Not Found Test")
    void createBoardTeamNotFoundTest() {

        BoardCreationRequest boardCreationRequest = new BoardCreationRequest();
        boardCreationRequest.setTeamId(UUID.randomUUID());

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.createBoard(ownerUser.getUserId(), boardCreationRequest));
    }
}
