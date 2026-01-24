package com.mordiniaa.backend.note.serviceRepo.archived;

import com.mordiniaa.backend.mappers.note.NoteMapper;
import com.mordiniaa.backend.mappers.note.dtoMappers.DeadlineNoteDtoMapper;
import com.mordiniaa.backend.mappers.note.dtoMappers.RegularNoteDtoMapper;
import com.mordiniaa.backend.models.note.Note;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.ArchivedNotesServiceImpl;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DataMongoTest
@Import({
        ArchivedNotesServiceImpl.class,
        NotesServiceImpl.class,
        NoteMapper.class,
        RegularNoteDtoMapper.class,
        DeadlineNoteDtoMapper.class
})
public class ArchivedNoteServiceSwitchArchivedTest {

    @MockitoBean(name = "mongoAuditor")
    private AuditorAware<String> mongoAuditor;

    @Autowired
    private ArchivedNotesServiceImpl archivedNotesService;

    @Autowired
    private NotesRepository notesRepository;


    @Autowired
    private MongoTemplate mongoTemplate;

    private final UUID ownerId = UUID.randomUUID();

    private RegularNote regularNote;

    @BeforeEach
    void setup() {
        when(mongoAuditor.getCurrentAuditor())
                .thenReturn(Optional.of("MONGO_TEST"));

        regularNote = new RegularNote();
        regularNote.setOwnerId(ownerId);
        regularNote.setTitle("Title");
        regularNote.setContent("Content");
        regularNote.setCategory(Category.DIARY);
    }

    @AfterEach
    void clear() {
        notesRepository.deleteAll();
    }

    @Test
    @DisplayName("Set Archived True Test")
    void setArchivedNoteTrue() {

        regularNote.setArchived(false);
        RegularNote savedNote = notesRepository.save(regularNote);

        ObjectId noteId = savedNote.getId();
        assertNotNull(noteId);

        assertFalse(savedNote.isArchived());

        assertDoesNotThrow(() -> archivedNotesService.switchArchivedNoteForUser(ownerId, noteId.toHexString()));

        Note updatedNote = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("_id").is(noteId)
                                .and("ownerId").is(ownerId)
                ),
                Note.class
        );

        assertNotNull(updatedNote);
        assertTrue(updatedNote.isArchived());
    }

    @Test
    @DisplayName("Set Archived False Test")
    void setArchivedNoteFalse() {

        regularNote.setArchived(true);
        RegularNote note = notesRepository.save(regularNote);

        ObjectId noteId = note.getId();

        assertNotNull(noteId);
        assertTrue(note.isArchived());

        assertDoesNotThrow(() -> archivedNotesService.switchArchivedNoteForUser(ownerId, noteId.toHexString()));

        Note updatedNote = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("_id").is(noteId)
                                .and("ownerId").is(ownerId)
                ),
                Note.class
        );

        assertNotNull(updatedNote);
        assertFalse(updatedNote.isArchived());
    }

    @Test
    @DisplayName("Exception Note Not Found")
    void exceptionNoteNotFoundTest() {

        assertThrows(RuntimeException.class, () -> archivedNotesService.switchArchivedNoteForUser(ownerId, ObjectId.get().toHexString()));
    }
}
