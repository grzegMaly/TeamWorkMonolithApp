package com.mordiniaa.backend.repositories.mongo;

import com.mordiniaa.backend.models.board.Board;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends MongoRepository<Board, ObjectId> {

    @Query(
        value = "{'_id': ?0}",
        fields = """
                {
                    'taskCategories': {$elemMatch: {'categoryName': ?1}},
                    'members': {$elemMatch: {'userId': ?2}}
                }
                """
    )
    Optional<Board> getBoardByIdWithCategoryAndBoardMember(ObjectId objectId, String categoryName, UUID userId);
}
