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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    private UserRepresentation ownerUser;

    private Team team;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

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
}
