package com.mordiniaa.backend.repositories.mongo;

import com.mordiniaa.backend.models.taskWorkspaces.Board;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, ObjectId> {
}
