package com.mordiniaa.backend.repositories.mongo.board.aggregation;

import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.UUID;

public interface BoardAggregationRepository {

    Optional<BoardMembersOnly> findBoardMembersForTask(ObjectId boardId, UUID userId, ObjectId taskId);
}
