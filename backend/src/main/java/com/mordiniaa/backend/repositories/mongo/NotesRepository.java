package com.mordiniaa.backend.repositories.mongo;

import com.mongodb.lang.Nullable;
import com.mordiniaa.backend.models.notes.Note;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotesRepository extends MongoRepository<Note, ObjectId> {

    Page<Note> findAllByOwnerId(UUID ownerId, Pageable pageable, @Nullable TextCriteria textCriteria);

    Optional<Note> findNoteByIdAndOwnerId(ObjectId id, UUID ownerId);
}
