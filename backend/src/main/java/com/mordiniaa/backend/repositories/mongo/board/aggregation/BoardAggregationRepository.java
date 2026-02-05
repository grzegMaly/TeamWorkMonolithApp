package com.mordiniaa.backend.repositories.mongo.board.aggregation;

import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersTasksOnly;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardWithTaskCategories;
import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BoardAggregationRepository {

    Optional<BoardMembersOnly> findBoardMembersForTask(ObjectId boardId, UUID userId, ObjectId taskId);

    Optional<BoardMembersOnly> findBoardMembers(ObjectId boardId, UUID userId, UUID teamId);

    Optional<BoardMembersTasksOnly> findBoardForTaskWithCategory(ObjectId boardId, UUID userId, ObjectId taskId);

    Optional<BoardWithTaskCategories> findBoardForTaskWithCategories(ObjectId boardId, UUID userId, ObjectId taskId);

    Set<Board> findAllBoardsForUserByUserIdAndTeamId(UUID userId, UUID teamId);

    Optional<BoardFull> findBoardWithTasksByUserIdAndBoardIdAndTeamId(UUID userId, ObjectId boardId, UUID teamId);

    Optional<Board> findFullBoardByIdAndOwnerAndExistingMember(ObjectId boardId, UUID ownerId, UUID userId);

    Optional<Board> findFullBoardByIdAndOwner(ObjectId boardId, UUID ownerId);
}
