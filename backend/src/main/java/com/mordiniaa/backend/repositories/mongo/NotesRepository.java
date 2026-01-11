package com.mordiniaa.backend.repositories.mongo;

import com.mongodb.client.result.UpdateResult;
import com.mordiniaa.backend.models.notes.Note;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotesRepository extends MongoRepository<Note, ObjectId> {

    Page<Note> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Page<Note> findAllByOwnerId(UUID ownerId, Pageable pageable, TextCriteria textCriteria);

    Optional<Note> findNoteByIdAndOwnerId(ObjectId id, UUID ownerId);

    long deleteByIdAndOwnerId(ObjectId id, UUID ownerId);

    void deleteAllByOwnerId(UUID ownerId);

    Page<Note> findAllByOwnerIdAndArchived(UUID ownerId, boolean archived, Pageable pageable);

    @Query("{'_id': ?1, 'ownerId': ?0}")
    @Update("{$bit:  {archived: {$xor: 1}}}")
    UpdateResult changeArchivedStatusForNote(UUID ownerId, ObjectId noteId);
}
