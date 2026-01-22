package com.mordiniaa.backend.repositories.mongo.user;

import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserRepresentationRepository extends MongoRepository<UserRepresentation, ObjectId> {

    boolean existsUserRepresentationByUserIdAndDeletedFalse(UUID userId);

    List<UserRepresentation> findAllByUserIdIn(Collection<UUID> userIds);
}
