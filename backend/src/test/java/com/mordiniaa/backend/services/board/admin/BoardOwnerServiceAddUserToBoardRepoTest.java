package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.dto.user.UserDto;
import com.mordiniaa.backend.models.board.Board;
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
import com.mordiniaa.backend.services.board.owner.BoardOwnerService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BoardOwnerServiceAddUserToBoardRepoTest {

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

    private Board board;

    private Role role;

    @BeforeEach
    void setup() {

        role = roleRepository.findRoleByAppRole(AppRole.ROLE_MANAGER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_MANAGER)));

        User user = new User();
        user.setUsername("Username");
        user.setRole(role);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setImageKey("KEY");
        user.setPassword("SuperSecretPassword");
        user = userRepository.save(user);

        ownerUser = new UserRepresentation();
        ownerUser.setUserId(user.getUserId());
        ownerUser.setUsername("Username");
        ownerUser.setImageKey("ImageURL");
        ownerUser = userRepresentationRepository.save(ownerUser);

        team = new Team();
        team.setTeamName("Test Team");
        team.setPresentationName("Test Team");
        team.setManager(user);
        team = teamRepository.save(team);

        BoardCreationRequest boardCreationRequest = new BoardCreationRequest();
        boardCreationRequest.setBoardName("BoardName");
        boardCreationRequest.setTeamId(team.getTeamId());
        String boardId = boardOwnerService.createBoard(ownerUser.getUserId(), boardCreationRequest).getBoardId();

        board = boardRepository.findById(new ObjectId(boardId))
                .orElseThrow();
    }

    @AfterEach
    void clear() {
        boardRepository.deleteAll();
        userRepresentationRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Add User To Board Test")
    void addUserToBoardTest() {

        User newUser = new User();
        newUser.setRole(role);
        newUser.setUsername("Username2");
        newUser.setPassword("SuperSecretPassword");
        newUser.setFirstName("First");
        newUser.setLastName("Last");
        newUser.setImageKey("KEY");
        newUser = userRepository.save(newUser);

        team.getTeamMembers().add(newUser);
        teamRepository.save(team);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(newUser.getUsername());
        userRepresentation.setUserId(newUser.getUserId());
        userRepresentation.setImageKey("Image2");
        userRepresentationRepository.save(userRepresentation);

        BoardDetailsDto boardDetailsDto = boardOwnerService.addUserToBoard(
                ownerUser.getUserId(),
                userRepresentation.getUserId(),
                board.getId().toHexString()
        );

        assertNotNull(boardDetailsDto);
        assertFalse(boardDetailsDto.getMembers().isEmpty());

        UserDto userDto = boardDetailsDto.getMembers().stream().filter(dto -> dto.getUserId().equals(userRepresentation.getUserId()))
                .findFirst().orElseThrow();
        assertEquals(userRepresentation.getImageKey(), userDto.getImageUrl());
        assertEquals(userRepresentation.getUsername(), userDto.getUsername());
    }

    @Test
    @DisplayName("Add User To Board User Inactive Test")
    void addUserToBoardUserInactiveTest() {

        UUID userId = UUID.randomUUID();
        UserRepresentation newUser = new UserRepresentation();
        newUser.setDeleted(true);
        newUser.setUserId(userId);
        newUser.setUsername("Username");
        newUser.setImageKey("ImageURL");
        userRepresentationRepository.save(newUser);

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.addUserToBoard(ownerUser.getUserId(), userId, board.getId().toHexString()));
    }

    @Test
    @DisplayName("Add User To Board Owner Inactive Test")
    void addUserToBoardOwnerInactiveTest() {

        UUID userId = UUID.randomUUID();
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUserId(userId);
        newUser.setUsername("Username");
        newUser.setImageKey("ImageURL");

        ownerUser.setDeleted(true);
        userRepresentationRepository.saveAll(List.of(ownerUser, newUser));

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.addUserToBoard(ownerUser.getUserId(), userId, board.getId().toHexString()));
    }

    @Test
    @DisplayName("Add User To Board User Not Found Test")
    void addUserToBoardUserNotFoundTest() {

        UUID userId = UUID.randomUUID();
        assertThrows(RuntimeException.class,
                () -> boardOwnerService.addUserToBoard(
                        ownerUser.getUserId(),
                        userId,
                        board.getId().toHexString()
                ));
    }

    @Test
    @DisplayName("Add User To Board User Not Board Member Test")
    void addUserToBoardUserNotBoarMemberTest() {

        UUID userId = UUID.randomUUID();
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("Username");
        newUser.setImageKey("ImageURL");
        newUser.setUserId(userId);
        userRepresentationRepository.save(newUser);

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.addUserToBoard(
                        ownerUser.getUserId(),
                        userId,
                        board.getId().toHexString()
                ));
    }

    @Test
    @DisplayName("Add User To Board User Not Team Member Test")
    void addUserToBoardUserNotTeamMemberTest() {

        User user = new User();
        user.setPassword("SuperSecretPassword");
        user.setUsername("New Username");
        user.setFirstName("LastName");
        user.setLastName("FirstName"); // Hehe
        user.setRole(role);
        user.setImageKey("KEY");
        user = userRepository.save(user);

        UserRepresentation newUR = new UserRepresentation();
        newUR.setUserId(user.getUserId());
        newUR.setImageKey("ImageURL");
        newUR.setUsername(user.getUsername());
        userRepresentationRepository.save(newUR);

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.addUserToBoard(ownerUser.getUserId(), newUR.getUserId(), board.getId().toHexString()));
    }

    @Test
    @DisplayName("Add User To Board User Not Board Owner Test")
    void addUserToBoardUserNotBoardOwnerTest() {

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUserId(UUID.randomUUID());
        newUser.setUsername("New Username");
        newUser.setImageKey("ImageURL");
        userRepresentationRepository.save(newUser);

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.addUserToBoard(
                        ownerUser.getUserId(),
                        newUser.getUserId(),
                        board.getId().toHexString()
                ));
    }
}
