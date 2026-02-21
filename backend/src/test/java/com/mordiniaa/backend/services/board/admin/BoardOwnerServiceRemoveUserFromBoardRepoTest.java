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

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BoardOwnerServiceRemoveUserFromBoardRepoTest {

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
    private UserRepresentation boardMember;

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
        user.setPassword("SuperSecretPassword");
        user.setImageKey("KEY");
        user = userRepository.save(user);

        User member = new User();
        member.setUsername("Member");
        member.setRole(role);
        member.setFirstName("MemberFirst");
        member.setLastName("MemberLast");
        member.setPassword("SuperSecretPassword");
        member.setImageKey("KEY");
        member = userRepository.save(member);

        ownerUser = new UserRepresentation();
        ownerUser.setUserId(user.getUserId());
        ownerUser.setUsername("Username");
        ownerUser.setImageKey("ImageURL");
        ownerUser = userRepresentationRepository.save(ownerUser);

        boardMember = new UserRepresentation();
        boardMember.setUserId(member.getUserId());
        boardMember.setUsername("Member");
        boardMember.setImageKey("ImageURL");
        boardMember = userRepresentationRepository.save(boardMember);

        team = new Team();
        team.setTeamName("Test Team");
        team.setPresentationName("Team Team");
        team.setManager(user);
        team.setTeamMembers(Set.of(member));
        team = teamRepository.save(team);

        BoardCreationRequest boardCreationRequest = new BoardCreationRequest();
        boardCreationRequest.setBoardName("BoardName");
        boardCreationRequest.setTeamId(team.getTeamId());
        String bId = boardOwnerService.createBoard(ownerUser.getUserId(), boardCreationRequest).getBoardId();

        ObjectId boardId = new ObjectId(bId);
        boardOwnerService.addUserToBoard(ownerUser.getUserId(), member.getUserId(), bId);

        board = boardRepository.findById(boardId)
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
    @DisplayName("Remove User From Board Valid Test")
    void removeUserFromBoardValidTest() {

        BoardDetailsDto dto = boardOwnerService.removeUserFromBoard(
                ownerUser.getUserId(),
                boardMember.getUserId(),
                board.getId().toHexString()
        );

        assertNotNull(dto);
        assertFalse(dto.getMembers().stream().map(UserDto::getUserId).collect(Collectors.toSet())
                .contains(boardMember.getUserId()));
    }

    @Test
    @DisplayName("Remove User From Board User Not Active Test")
    void removeUserFromBoardUserNotActiveTest() {

        ownerUser.setDeleted(true);
        userRepresentationRepository.save(ownerUser);
        assertThrows(RuntimeException.class,
                () -> boardOwnerService.removeUserFromBoard(
                        ownerUser.getUserId(),
                        boardMember.getUserId(),
                        board.getId().toHexString()
                ));
    }

    @Test
    @DisplayName("Remove User From Board User Not Board Owner Test")
    void removeUserFromBoardUserNotBoardOwnerTest() {

        UUID userId = UUID.randomUUID();
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("New Username");
        newUser.setImageKey("Image");
        newUser.setUserId(userId);
        userRepresentationRepository.save(newUser);

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.removeUserFromBoard(
                        userId,
                        boardMember.getUserId(),
                        board.getId().toHexString()
                ));
    }
}
