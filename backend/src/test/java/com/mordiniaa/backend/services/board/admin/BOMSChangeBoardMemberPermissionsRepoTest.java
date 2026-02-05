package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.request.board.PermissionsRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class BOMSChangeBoardMemberPermissionsRepoTest {

    @Autowired
    private BoardOwnerManagementService boardOwnerManagementService;

    @Autowired
    private UserRepresentationRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    private final UUID ownerId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();

    private Board board;

    private BoardMember ownerMember;
    private BoardMember member;

    private UserRepresentation ownerUser;
    private UserRepresentation memberUser;

    @BeforeEach
    void setup() {

        ownerUser = new UserRepresentation();
        ownerUser.setUserId(ownerId);
        ownerUser.setUsername("weewrge");
        ownerUser.setImageUrl("ererge");
        ownerUser = userRepository.save(ownerUser);

        memberUser = new UserRepresentation();
        memberUser.setUserId(memberId);
        memberUser.setImageUrl("evrertgh");
        memberUser.setUsername("ergvertg");
        memberUser = userRepository.save(memberUser);

        ownerMember = new BoardMember(ownerId);
        member = new BoardMember(memberId);

        board = new Board();
        board.setBoardName("vwerverg");
        board.setTeamId(teamId);
        board.setOwner(ownerMember);
        board.setMembers(List.of(member));
        board.setTeamId(teamId);
        board = boardRepository.save(board);
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("Change Board Member Permissions Valid Test")
    void changeBoardMemberPermissionsValidTest() {

        PermissionsRequest permissionsRequest = new PermissionsRequest();
        permissionsRequest.setCategoryPermissions(Set.of(CategoryPermissions.values()));
        permissionsRequest.setTaskPermissions(Set.of(TaskPermission.values()));
        permissionsRequest.setCommentPermissions(Set.of(CommentPermission.values()));
        permissionsRequest.setBoardPermissions(Set.of(BoardPermission.values()));

        assertDoesNotThrow(() -> boardOwnerManagementService.changeBoardMemberPermissions(
                ownerId,
                board.getId().toHexString(),
                memberId,
                permissionsRequest
        ));

        Board updatedBoard = boardRepository.findById(board.getId())
                .orElseThrow(RuntimeException::new);

        BoardMember updatedMember = updatedBoard.getMembers().stream().filter(bm -> bm.getUserId().equals(memberId))
                .findFirst().orElseThrow();

        assertTrue(permissionsRequest.getCategoryPermissions().containsAll(updatedMember.getCategoryPermissions()));
        assertTrue(permissionsRequest.getCommentPermissions().containsAll(updatedMember.getCommentPermissions()));
        assertTrue(permissionsRequest.getTaskPermissions().containsAll(updatedMember.getTaskPermissions()));
        assertTrue(permissionsRequest.getBoardPermissions().containsAll(updatedMember.getBoardPermissions()));
    }

    @Test
    @DisplayName("Change User Permissions User Deleted Test")
    void changeUserPermissionUserDeletedTest() {

        memberUser.setDeleted(true);
        userRepository.save(memberUser);
        assertThrows(RuntimeException.class,
                () -> boardOwnerManagementService.changeBoardMemberPermissions(
                        ownerId,
                        board.getId().toHexString(),
                        memberId,
                        new PermissionsRequest())
        );
    }
}
