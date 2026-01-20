package com.mordiniaa.backend.repositories.mongo.board;

import com.mordiniaa.backend.models.board.BoardMembersOnly;
import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.UUID;

public interface BoardAggregationRepository {

    Optional<BoardMembersOnly> findBoardMembersForTask(ObjectId boardId, UUID userId, ObjectId taskId);
}
