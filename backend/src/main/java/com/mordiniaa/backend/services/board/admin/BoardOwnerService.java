package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.request.board.BoardCreationRequest;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardOwnerService {

    private final MongoUserService mongoUserService;
    private final TeamRepository teamRepository;
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final BoardAggregationRepositoryImpl boardAggregationRepositoryImpl;
    private final MongoIdUtils mongoIdUtils;

    public BoardDetailsDto createBoard(UUID userId, BoardCreationRequest boardCreationRequest) {

        mongoUserService.checkUserAvailability(userId);
        UUID teamId = boardCreationRequest.getTeamId();

        if (!teamRepository.existsTeamByTeamIdAndManager_UserId(teamId, userId))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Board board = new Board();
        board.setTeamId(teamId);
        board.setBoardName(boardCreationRequest.getBoardName());

        BoardMember ownerMember = new BoardMember(userId);
        ownerMember.setBoardPermissions(Set.of(BoardPermission.values()));
        ownerMember.setCategoryPermissions(Set.of(CategoryPermissions.values()));
        ownerMember.setTaskPermissions(Set.of(TaskPermission.values()));
        ownerMember.setCommentPermissions(Set.of(CommentPermission.values()));

        board.setOwner(ownerMember);

        Board savedBoard = boardRepository.save(board);
        BoardFull aggregatedBoardDocument = boardAggregationRepositoryImpl
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(userId, savedBoard.getId(), teamId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        return boardMapper.toDetailedDto(aggregatedBoardDocument);
    }

    public void addUserToBoard(UUID boardOwner, UUID userId, String bId) {

        mongoUserService.checkUserAvailability(boardOwner, userId);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        Board board = boardAggregationRepositoryImpl.findFullBoardByIdAndOwner(boardId, boardOwner)
                .orElseThrow(RuntimeException::new);


        UUID teamId = board.getTeamId();
        if (!teamRepository.existsUserInTeam(teamId, userId))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        BoardMember newMember = new BoardMember(userId);
        newMember.setBoardPermissions(Set.of(BoardPermission.VIEW_BOARD));
        newMember.setCommentPermissions(Set.of(CommentPermission.COMMENT_TASK, CommentPermission.DELETE_OWN_COMMENT));

        board.addMember(newMember);
        boardRepository.save(board);
    }

    public void removeUserFromBoard(UUID boardOwner, UUID userId, String bId) {

        mongoUserService.checkUserAvailability(boardOwner);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        Board board = boardAggregationRepositoryImpl.findFullBoardByIdAndOwner(boardId, boardOwner)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
        board.removeMember(userId);
        boardRepository.save(board);
    }

    public void updateBoardDetails() {

    }

    public void deleteBoard() {

    }
}
