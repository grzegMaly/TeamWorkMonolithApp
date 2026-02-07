package com.mordiniaa.backend.services.board.admin;

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
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class BoardOwnerServiceDeleteBoardRepoTest {

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

        User member = new User();
        member.setUsername("Member");
        member.setRole(managerRole);
        member.setEmail("member@gmail.com");
        member.setFirstName("MemberFirst");
        member.setLastName("MemberLast");
        member.setPassword("SuperSecretPassword");
        member = userRepository.save(member);

        ownerUser = new UserRepresentation();
        ownerUser.setUserId(user.getUserId());
        ownerUser.setUsername("Username");
        ownerUser.setImageUrl("ImageURL");
        ownerUser = userRepresentationRepository.save(ownerUser);

        boardMember = new UserRepresentation();
        boardMember.setUserId(member.getUserId());
        boardMember.setUsername("Member");
        boardMember.setImageUrl("ImageURL");
        boardMember = userRepresentationRepository.save(boardMember);

        team = new Team();
        team.setTeamName("Test Team");
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

    @Test
    @DisplayName("Delete Board Valid Test")
    void deleteBoardValidTest() {
        assertDoesNotThrow(() -> boardOwnerService.deleteBoard(
                ownerUser.getUserId(),
                board.getId().toHexString()
        ));

        board = boardRepository.findById(board.getId())
                .orElseThrow();
        assertTrue(board.isDeleted());
        assertTrue(board.isArchived());
    }

    @Test
    @DisplayName("Delete Board User Not Owner Test")
    void deleteBoardUserNotOwnerTest() {

        assertThrows(RuntimeException.class,
                () -> boardOwnerService.deleteBoard(
                        boardMember.getUserId(),
                        board.getId().toHexString()
                ));

        board = boardRepository.findById(board.getId())
                .orElseThrow();
        assertFalse(board.isDeleted());
        assertFalse(board.isArchived());
    }
}
