package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.request.board.PermissionsRequest;
import com.mordiniaa.backend.services.board.owner.BoardOwnerManagementService;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BOMSChangeBoardMemberPermissionsMockTest {

    @InjectMocks
    private BoardOwnerManagementService boardOwnerManagementService;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private BoardAggregationRepository boardAggregationRepository;

    @Mock
    private BoardUtils boardUtils;

    @Mock
    private MongoIdUtils mongoIdUtils;

    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("Change Board Member Permissions Test")
    void changeBoardMemberPermissionsTest() {

        UUID ownerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(ownerId, memberId);

        ObjectId boardId = ObjectId.get();
        when(mongoIdUtils.getObjectId(anyString()))
                .thenReturn(boardId);

        Board board = mock(Board.class);
        when(boardAggregationRepository.findFullBoardByIdAndOwnerAndExistingMember(boardId, ownerId, memberId))
                .thenReturn(Optional.of(board));

        BoardMember member = new BoardMember(memberId);
        when(boardUtils.getBoardMember(board, memberId))
                .thenReturn(member);

        when(boardRepository.save(board))
                .thenReturn(null);

        PermissionsRequest permissionsRequest = new PermissionsRequest();
        permissionsRequest.setBoardPermissions(Set.of(BoardPermission.values()));
        permissionsRequest.setCommentPermissions(Set.of(CommentPermission.values()));
        permissionsRequest.setTaskPermissions(Set.of(TaskPermission.values()));
        permissionsRequest.setCategoryPermissions(Set.of(CategoryPermissions.values()));

        assertDoesNotThrow(() -> boardOwnerManagementService.changeBoardMemberPermissions(ownerId, boardId.toHexString(), memberId, permissionsRequest));
    }
}
