package com.mordiniaa.backend.repositories.mongo.board;

import com.mordiniaa.backend.models.board.BoardMembersOnly;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Repository
@RequiredArgsConstructor
public class BoardAggregationRepositoryImpl implements BoardAggregationRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<BoardMembersOnly> findBoardMembersForTask(ObjectId boardId, UUID userId, ObjectId taskId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(boardId)),
                match(new Criteria().orOperator(
                        Criteria.where("owner.userId").is(userId),
                        Criteria.where("members.userId").is(userId)
                )),
                match(Criteria.where("taskCategories.tasks").is(taskId)),
                project("owner", "members")
        );

        return mongoTemplate
                .aggregate(aggregation, "boards", BoardMembersOnly.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }
}
