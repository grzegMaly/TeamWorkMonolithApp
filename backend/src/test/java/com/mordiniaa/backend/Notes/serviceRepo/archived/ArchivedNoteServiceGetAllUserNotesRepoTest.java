package com.mordiniaa.backend.Notes.serviceRepo.archived;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.mappers.notes.dtoMappers.DeadlineNoteDtoMapper;
import com.mordiniaa.backend.mappers.notes.dtoMappers.RegularNoteDtoMapper;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.ArchivedNotesServiceImpl;
import com.mordiniaa.backend.utils.PageResult;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DataMongoTest
@Import({
        NoteMapper.class,
        ArchivedNotesServiceImpl.class,
        RegularNoteDtoMapper.class,
        DeadlineNoteDtoMapper.class
})
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
        deadlineNote.setTitle("Title");
        deadlineNote.setContent("Content");

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
