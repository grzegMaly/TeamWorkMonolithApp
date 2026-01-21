package com.mordiniaa.backend.repositories.mongo.board.aggregation;

import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersTasksOnly;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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

    @Override
    public Optional<BoardMembersTasksOnly> findBoardWithSpecifiedMemberOnly(ObjectId boardId, UUID userId, ObjectId taskId) {

        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("tasks")
                .let(VariableOperators.Let.ExpressionVariable
                        .newVariable("taskIds")
                        .forField("$taskCategories.tasks"))
                .pipeline(
                        Aggregation.match(
                                Criteria.expr(() ->
                                        new Document("$in", List.of("$_id", "$$taskIds"))
                                )
                        ),
                        Aggregation.project("createdBy")
                )
                .as("tasks");


        Aggregation aggr = Aggregation.newAggregation(
                match(Criteria.where("_id").is(boardId)),
                match(new Criteria().orOperator(
                        Criteria.where("owner.userId").is(userId),
                        Criteria.where("members.userId").is(userId)
                )),
                unwind("taskCategories"),
                match(Criteria.where("taskCategories.tasks").is(taskId)),
                lookupOperation,
                group("_id")
                        .first("owner").as("owner")
                        .first("members").as("members")
                        .first("tasks").as("tasks")
        );
        return mongoTemplate.aggregate(aggr, "boards", BoardMembersTasksOnly.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }
}
