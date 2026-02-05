package com.mordiniaa.backend.repositories.mongo.board;

import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends MongoRepository<Board, ObjectId> {
    @Aggregation(pipeline = {
            "{$match: {_id: ?0}}",
            "{$match:  {$or: [{'owner.userId': ?2}, {'members.userId': ?2}]}}",
            """
                    {
                        $project: {
                                owner: 1,
                                taskCategories: {
                                            $filter: {
                                                input: "$taskCategories",
                                                as: "cat",
                                                cond: {$eq:  ["$$cat.categoryName", ?1]}}},
                                members: {
                                            $map: {
                                                        input: "$members",
                                                        as: "m",
                                                        in: {
                                                            $cond: [{$eq: ["$$m.userId", ?2]},
                                                                    "$$m",
                                                                    {userId: "$$m.userId"}]
                                                            }
                                                }
                                        }
                                }
                    }
                    """
    })
    Optional<Board> getBoardByIdWithCategoryAndBoardMemberOrOwner(ObjectId objectId, String categoryName, UUID userId);

    Optional<Board> findBoardByIdAndOwner_UserIdAndTeamId(ObjectId id, UUID ownerUserId, UUID teamId);
}
