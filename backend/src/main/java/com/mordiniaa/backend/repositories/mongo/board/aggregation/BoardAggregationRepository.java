package com.mordiniaa.backend.repositories.mongo.board.aggregation;

import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersTasksOnly;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardWithTaskCategories;
import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.UUID;

public interface BoardAggregationRepository {

    Optional<BoardMembersOnly> findBoardMembersForTask(ObjectId boardId, UUID userId, ObjectId taskId);

    Optional<BoardMembersTasksOnly> findBoardWithSpecifiedMemberOnly(ObjectId boardId, UUID userId, ObjectId taskId);

    Optional<BoardWithTaskCategories> findBoardForTaskWithCategories(ObjectId boardId, UUID userId, ObjectId taskId);
}
