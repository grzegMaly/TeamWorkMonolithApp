package com.mordiniaa.backend.services.board.admin;

import com.mongodb.client.result.UpdateResult;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.request.board.PermissionsRequest;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardOwnerManagementService {

    private final MongoUserService mongoUserService;
    private final MongoIdUtils mongoIdUtils;
    private final BoardRepository boardRepository;
    private final BoardAggregationRepository boardAggregationRepository;
    private final BoardUtils boardUtils;
    private final TeamRepository teamRepository;
    private final MongoTemplate mongoTemplate;

    public void changeBoardMemberPermissions(UUID ownerId, String bId, UUID userId, PermissionsRequest permissionsRequest) {

        mongoUserService.checkUserAvailability(ownerId, userId);

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        Board board = boardAggregationRepository.findFullBoardByIdAndOwnerAndExistingMember(boardId, ownerId, userId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        BoardMember member = boardUtils.getBoardMember(board, userId);

        member.setBoardPermissions(permissionsRequest.getBoardPermissions());
        member.setCategoryPermissions(permissionsRequest.getCategoryPermissions());
        member.setTaskPermissions(permissionsRequest.getTaskPermissions());
        member.setCommentPermissions(permissionsRequest.getCommentPermissions());
        boardRepository.save(board);
    }

    public void archiveBoard(UUID ownerId, String bId) {
        setBoardArchivedStatus(ownerId, bId, true);
    }

    public void restoreBoard(UUID ownerId, String bId) {
        setBoardArchivedStatus(ownerId, bId, false);
    }

    private void setBoardArchivedStatus(UUID ownerId, String bId, boolean status) {

        mongoUserService.checkUserAvailability(ownerId);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        Query updateQuery = Query.query(
                Criteria.where("_id").is(boardId)
                        .and("owner.userId").is(ownerId)
                        .and("deleted").is(false)
                        .and("archived").ne(status)
        );
        Update update = new Update()
                .set("archived", status);

        UpdateResult result = mongoTemplate.updateFirst(updateQuery, update, Board.class);
        if (result.getModifiedCount() == 0)
            throw new RuntimeException(); // TODO: Change In Exceptions Section
    }
}
