package com.mordiniaa.backend.services.board.admin;

import com.mongodb.client.result.UpdateResult;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private final MongoTemplate mongoTemplate;

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

    public BoardDetailsDto addUserToBoard(UUID boardOwner, UUID userId, String bId) {

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
        BoardFull savedBoard = boardAggregationRepositoryImpl
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(boardOwner, boardId, teamId)
                .orElseThrow(RuntimeException::new);
        return boardMapper.toDetailedDto(savedBoard);
    }

    public BoardDetailsDto removeUserFromBoard(UUID boardOwner, UUID userId, String bId) {

        mongoUserService.checkUserAvailability(boardOwner);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        Board board = boardAggregationRepositoryImpl.findFullBoardByIdAndOwner(boardId, boardOwner)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
        board.removeMember(userId);
        boardRepository.save(board);
        BoardFull savedBoard = boardAggregationRepositoryImpl
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(boardOwner, boardId, board.getTeamId())
                .orElseThrow(RuntimeException::new);
        return boardMapper.toDetailedDto(savedBoard);
    }

    public void deleteBoard(UUID boardOwner, String bId) {

        mongoUserService.checkUserAvailability(boardOwner);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        Update update = new Update()
                .set("archived", true)
                .set("deleted", true);
        Query updateQuery = Query.query(
                new Criteria().andOperator(
                        Criteria.where("_id").is(boardId),
                        Criteria.where("owner.userId").is(boardOwner)
                )
        );

        UpdateResult result = mongoTemplate.updateFirst(updateQuery, update, Board.class);
        if (result.getModifiedCount() == 0)
            throw new RuntimeException(); // TODO: Change In Exceptions Section
    }
}
