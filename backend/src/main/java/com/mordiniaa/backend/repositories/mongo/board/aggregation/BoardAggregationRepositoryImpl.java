package com.mordiniaa.backend.repositories.mongo.board.aggregation;

import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersTasksOnly;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardWithTaskCategories;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.*;

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
                project("owner", "members", "boardName", "createdAt", "updatedAt")
        );

        return mongoTemplate
                .aggregate(aggregation, "boards", BoardMembersOnly.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<BoardMembersOnly> findBoardMembers(ObjectId boardId, UUID userId, UUID teamId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(boardId)),
                match(new Criteria().orOperator(
                        Criteria.where("owner.userId").is(userId),
                        Criteria.where("members.userId").is(userId)
                )),
                match(Criteria.where("teamId").is(teamId)),
                project("owner", "members", "boardName", "createdAt", "updatedAt")
        );

        return mongoTemplate
                .aggregate(aggregation, "boards", BoardMembersOnly.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<BoardMembersTasksOnly> findBoardForTaskWithCategory(ObjectId boardId, UUID userId, ObjectId taskId) {

        Aggregation aggr = Aggregation.newAggregation(
                match(Criteria.where("_id").is(boardId)),
                match(new Criteria().orOperator(
                        Criteria.where("owner.userId").is(userId),
                        Criteria.where("members.userId").is(userId)
                )),
                unwind("taskCategories"),
                LookupOperation.newLookup()
                        .from("tasks")
                        .let(VariableOperators.Let.ExpressionVariable
                                .newVariable("taskIds")
                                .forField("$taskCategories.tasks"))
                        .pipeline(Aggregation.match(
                                        Criteria.expr(() -> new Document("$in", List.of("$_id", "$$taskIds")))
                                ),
                                Aggregation.project()
                                        .and("_id").as("id")
                                        .and("createdBy").as("createdBy")
                                        .and(
                                                ConditionalOperators.when(
                                                                ComparisonOperators.Eq.valueOf("_id").equalToValue(taskId)
                                                        )
                                                        .thenValueOf("positionInCategory")
                                                        .otherwise("$$REMOVE")
                                        ).as("taskPosition")
                        )
                        .as("tasks"),
                match(Criteria.where("taskCategories.tasks").is(taskId)),
                project("id", "owner", "members", "tasks")
        );

        return mongoTemplate.aggregate(aggr, "boards", BoardMembersTasksOnly.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<BoardWithTaskCategories> findBoardForTaskWithCategories(ObjectId boardId, UUID userId, ObjectId taskId) {

        Aggregation aggr = Aggregation.newAggregation(
                match(Criteria.where("_id").is(boardId)),
                match(new Criteria().orOperator(
                        Criteria.where("owner.userId").is(userId),
                        Criteria.where("members.userId").is(userId)
                )),
                match(Criteria.where("taskCategories.tasks").is(taskId)),
                project("owner", "members", "taskCategories")
        );

        return mongoTemplate.aggregate(aggr, "boards", BoardWithTaskCategories.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }

    @Override
    public Set<Board> findAllBoardsForUserByUserIdAndTeamId(UUID userId, UUID teamId) {

        Criteria aggrCriteria = new Criteria()
                .andOperator(
                        Criteria.where("teamId").is(teamId),
                        new Criteria()
                                .orOperator(
                                        Criteria.where("owner.userId").is(userId),
                                        Criteria.where("members.userId").is(userId)
                                )
                );

        Aggregation aggregation = Aggregation.newAggregation(
                match(aggrCriteria)
        );
        return new HashSet<>(mongoTemplate.aggregate(aggregation, "boards", Board.class)
                .getMappedResults());
    }

    @Override
    public Optional<BoardFull> findBoardWithTasksByUserIdAndBoardIdAndTeamId(UUID userId, ObjectId boardId, UUID teamId) {

        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("_id").is(boardId),
                        Criteria.where("teamId").is(teamId),
                        new Criteria()
                                .orOperator(
                                        Criteria.where("owner.userId").is(userId),
                                        Criteria.where("members.userId").is(userId)
                                )
                );

        LookupOperation ownerLookup = LookupOperation.newLookup()
                .from("users")
                .localField("owner.userId")
                .foreignField("userId")
                .as("owner");
        UnwindOperation ownerUnwind = Aggregation.unwind("owner");

        LookupOperation membersLookup = LookupOperation.newLookup()
                .from("users")
                .localField("members.userId")
                .foreignField("userId")
                .as("members");

        LookupOperation tasksLookup = LookupOperation.newLookup()
                .from("tasks")
                .let(VariableOperators.Let.ExpressionVariable
                        .newVariable("taskIds")
                        .forField("$taskCategories.tasks")
                )
                .pipeline(Aggregation.match(
                        Criteria.expr(() -> new Document("$in", List.of("$_id", "$$taskIds")))
                ))
                .as("taskCategories.tasks");

        GroupOperation group = Aggregation.group("_id")
                .first("owner").as("owner")
                .first("members").as("members")
                .first("teamId").as("teamId")
                .first("boardName").as("boardName")
                .push("taskCategories").as("taskCategories")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt");

        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                ownerLookup,
                ownerUnwind,
                membersLookup,
                unwind("taskCategories"),
                tasksLookup,
                group
        );
        return mongoTemplate.aggregate(aggregation, "boards", BoardFull.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Board> findFullBoardByIdAndOwnerAndExistingMember(ObjectId boardId, UUID ownerId, UUID userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(
                        new Criteria().andOperator(
                                Criteria.where("_id").is(boardId),
                                Criteria.where("owner.userId").is(ownerId),
                                Criteria.where("members.userId").is(userId)
                        )
                )
        );

        return mongoTemplate.aggregate(aggregation, "boards", Board.class)
                .getMappedResults()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Board> findFullBoardByIdAndOwner(ObjectId boardId, UUID ownerId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(new Criteria().andOperator(
                                Criteria.where("_id").is(boardId),
                                Criteria.where("owner.userId").is(ownerId)
                        )
                )
        );
        return mongoTemplate.aggregate(aggregation, "boards", Board.class)
                .getMappedResults()
                .stream().findFirst();
    }
}
