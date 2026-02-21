package com.mordiniaa.backend.services.note;

import com.mordiniaa.backend.dto.note.NoteDto;
import com.mordiniaa.backend.models.note.Note;
import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.ArchivedNotesServiceImpl;
import com.mordiniaa.backend.utils.PageResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ArchivedNoteServiceGetAllUserNotesRepoTest {

    @MockitoBean(name = "mongoAuditor")
    private AuditorAware<String> mongoAuditor;

    @Autowired
    private ArchivedNotesServiceImpl archivedNotesService;

    @Autowired
    private NotesRepository notesRepository;

    private final UUID ownerIdOne = UUID.randomUUID();
    private final UUID ownerIdTwo = UUID.randomUUID();

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        when(mongoAuditor.getCurrentAuditor())
                .thenReturn(Optional.of("MONGO_TEST"));

        for (int i = 0; i < 2; i++) {

            boolean archived = i % 2 == 0;
            for (int j = 0; j < 13; j++) {

                RegularNote regularNote = getRegularNote(archived);
                regularNote.setOwnerId(ownerIdOne);
                notesRepository.save(regularNote);

                regularNote = getRegularNote(archived);
                regularNote.setOwnerId(ownerIdTwo);
                notesRepository.save(regularNote);

                DeadlineNote deadlineNote = getDeadlineNote(archived);
                deadlineNote.setOwnerId(ownerIdOne);
                notesRepository.save(deadlineNote);

                deadlineNote = getDeadlineNote(archived);
                deadlineNote.setOwnerId(ownerIdTwo);
                notesRepository.save(deadlineNote);
            }
        }
    }

    private RegularNote getRegularNote(boolean archived) {
        RegularNote regularNote = new RegularNote();
        regularNote.setArchived(archived);
        regularNote.setCategory(Category.DIARY);
        regularNote.setTitle("Title");
        regularNote.setContent("Content");

        return regularNote;
    }

    private DeadlineNote getDeadlineNote(boolean archived) {
        DeadlineNote deadlineNote = new DeadlineNote();
        deadlineNote.setArchived(archived);
        deadlineNote.setStatus(Status.CANCELED);
        deadlineNote.setPriority(Priority.LOW);
        deadlineNote.setTitle("Title");
        deadlineNote.setContent("Content");
        deadlineNote.setDeadline(Instant.now().plus(2, ChronoUnit.DAYS));

        return deadlineNote;
    }

    @AfterEach
    void clear() {
        notesRepository.deleteAll();
    }

    @Test
    @DisplayName("All Notes User One Archived True Test")
    void getAllArchivedNotesForUserOne() {

        PageResult<List<NoteDto>> pageResult = archivedNotesService.fetchAllArchivedNotes(ownerIdOne, 0, 30);
        Set<String> dtoIds = pageResult.getData().stream()
                .map(NoteDto::getId)
                .collect(Collectors.toSet());

        Query query = new Query();
        query.addCriteria(
                Criteria.where("archived").is(true)
                        .and("ownerId").is(ownerIdOne)
        ).limit(30);

        Set<String> noteIds = mongoTemplate.find(query, Note.class).stream()
                .map(Note::getId)
                .map(ObjectId::toHexString)
                .collect(Collectors.toSet());

        assertEquals(26, dtoIds.size());
        assertEquals(dtoIds.size(), noteIds.size());

        assertTrue(noteIds.containsAll(dtoIds));
    }

    @Test
    @DisplayName("All Notes User Two Archived True Test")
    void getAllArchivedNotesForUserTwo() {

        PageResult<List<NoteDto>> pageResult = archivedNotesService.fetchAllArchivedNotes(ownerIdTwo, 0, 30);
        Set<String> dtoIds = pageResult.getData().stream()
                .map(NoteDto::getId)
                .collect(Collectors.toSet());

        Query query = new Query();
        query.addCriteria(
                Criteria.where("archived").is(true)
                        .and("ownerId").is(ownerIdTwo)
        ).limit(30);

        Set<String> noteIds = mongoTemplate.find(query, Note.class).stream()
                .map(Note::getId)
                .map(ObjectId::toHexString)
                .collect(Collectors.toSet());

        assertEquals(26, dtoIds.size());
        assertEquals(dtoIds.size(), noteIds.size());

        assertTrue(noteIds.containsAll(dtoIds));
    }

    @Test
    @DisplayName("No Archived Notes For User")
    void getAllArchivedNotesForUserNone() {

        UUID randomUser = UUID.randomUUID();

        PageResult<List<NoteDto>> pageResult =
                archivedNotesService.fetchAllArchivedNotes(randomUser, 0, 10);

        assertNotNull(pageResult);
        assertNotNull(pageResult.getData());
        assertTrue(pageResult.getData().isEmpty());
    }
}
