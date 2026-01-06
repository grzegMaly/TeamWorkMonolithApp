package com.mordiniaa.backend.repositories;

import com.mordiniaa.backend.models.notes.Note;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotesRepository extends MongoRepository<Note, ObjectId> {
}
