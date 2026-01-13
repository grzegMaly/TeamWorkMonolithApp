package com.mordiniaa.backend.repositories.mongo;

import com.mordiniaa.backend.models.user.mongodb.UserProjection;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProjectionRepository extends MongoRepository<UserProjection, ObjectId> {
}
