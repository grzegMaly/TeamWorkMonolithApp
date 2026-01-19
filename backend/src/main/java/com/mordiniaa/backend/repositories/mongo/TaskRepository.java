package com.mordiniaa.backend.repositories.mongo;

import com.mordiniaa.backend.models.task.Task;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, ObjectId> {

}
