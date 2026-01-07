package com.mordiniaa.backend.repositories.mongo;

import com.mordiniaa.backend.models.notes.Note;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotesRepository extends MongoRepository<Note, ObjectId> {

    List<Note> findAllByOwnerId(UUID ownerId);

    Optional<Note> findNoteByIdAndOwnerId(ObjectId id, UUID ownerId);
}
