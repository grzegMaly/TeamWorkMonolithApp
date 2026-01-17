package com.mordiniaa.backend.note.serviceRepo;

import com.mordiniaa.backend.dto.note.DeadlineNoteDto;
import com.mordiniaa.backend.dto.note.NoteDto;
import com.mordiniaa.backend.dto.note.RegularNoteDto;
import com.mordiniaa.backend.mappers.note.NoteMapper;
import com.mordiniaa.backend.mappers.note.dtoMappers.DeadlineNoteDtoMapper;
import com.mordiniaa.backend.mappers.note.dtoMappers.RegularNoteDtoMapper;
import com.mordiniaa.backend.mappers.note.modelMappers.DeadlineNoteModelMapper;
import com.mordiniaa.backend.mappers.note.modelMappers.RegularNoteModelMapper;
import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.deadline.PatchDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.regular.PatchRegularNoteRequest;
import com.mordiniaa.backend.services.notes.notes.NotesServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DataMongoTest
@Import({
        NoteMapper.class,
        RegularNoteDtoMapper.class,
        DeadlineNoteDtoMapper.class,
        RegularNoteModelMapper.class,
        DeadlineNoteModelMapper.class,
        NotesServiceImpl.class
})
public class NoteServiceUpdateNoteRepoTest {

    @MockitoBean(name = "mongoAuditor")
    AuditorAware<String> mongoAuditor;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private NotesServiceImpl notesService;

    private final UUID ownerId = UUID.randomUUID();

    private final String title = "Title";
    private final String content = "Content";

    private final Category category = Category.DIARY;

    private final Priority priority = Priority.HIGH;
    private final Status status = Status.CANCELED;
    private Instant deadline = Instant.now().plus(3, ChronoUnit.DAYS);

    private RegularNote regularNote;
    private DeadlineNote deadlineNote;

    @BeforeEach
    void setup() {
        when(mongoAuditor.getCurrentAuditor())
                .thenReturn(Optional.of("MONGO_TEST"));

        regularNote = notesRepository.save(RegularNote.builder()
                .ownerId(ownerId)
                .title(title)
                .content(content)
                .category(category)
                .build()
        );
        regularNote = (RegularNote) notesRepository.findNoteByIdAndOwnerId(regularNote.getId(), ownerId)
                .orElseThrow(RuntimeException::new);

        deadlineNote = notesRepository.save(DeadlineNote.builder()
                .ownerId(ownerId)
                .title(title)
                .content(content)
                .priority(priority)
                .status(status)
                .deadline(deadline)
                .build()
        );
        deadlineNote = (DeadlineNote) notesRepository.findNoteByIdAndOwnerId(deadlineNote.getId(), ownerId)
                .orElseThrow(RuntimeException::new);
        deadline = deadlineNote.getDeadline();
    }

    @AfterEach
    void clean() {
        notesRepository.deleteAll();
    }

    @Test
    @DisplayName("Regular Note Update Title Test")
    void regularNoteUpdateTitleTest() {

        String updatedTitle = "X";
        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setTitle(updatedTitle);

        String noteId = regularNote.getId().toHexString();
        NoteDto updatedNote = notesService.updateNote(ownerId, noteId, patchRegularNoteRequest);

        assertNotNull(updatedNote);
        assertNotNull(updatedNote.getCreatedAt());
        assertNotNull(updatedNote.getUpdatedAt());

        assertEquals(ownerId, updatedNote.getOwnerId());
        assertEquals(updatedTitle, updatedNote.getTitle(), "Titles should be the same");

        assertTrue(updatedNote.getUpdatedAt().isAfter(updatedNote.getCreatedAt()));
    }

    @Test
    @DisplayName("Regular Note Update Category Test")
    void regularNoteUpdateCategoryTest() {

        Category updatedCategory = Category.MEETING;
        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setCategory(updatedCategory);

        String noteId = regularNote.getId().toString();
        RegularNoteDto updatedNote = (RegularNoteDto) notesService.updateNote(ownerId, noteId, patchRegularNoteRequest);

        assertNotNull(updatedNote);
        assertNotNull(updatedNote.getCreatedAt());
        assertNotNull(updatedNote.getUpdatedAt());

        assertEquals(ownerId, updatedNote.getOwnerId());
        assertEquals(updatedCategory, updatedNote.getCategory());

        assertTrue(updatedNote.getUpdatedAt().isAfter(updatedNote.getCreatedAt()));
    }

    @Test
    @DisplayName("Regular Note Update Title Empty String Test")
    void regularNoteUpdateTitleEmptyStringTest() {

        String updatedTitle = "   ";
        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setTitle(updatedTitle);

        String noteId = regularNote.getId().toHexString();
        RegularNoteDto updatedNote = (RegularNoteDto) notesService.updateNote(ownerId, noteId, patchRegularNoteRequest);

        assertNotNull(updatedNote);

        assertNotEquals(updatedTitle, updatedNote.getTitle());
        assertNotNull(updatedNote.getTitle());
        assertEquals(title, updatedNote.getTitle());

        assertTrue(updatedNote.getUpdatedAt().isAfter(updatedNote.getCreatedAt()));
    }

    @Test
    @DisplayName("Deadline Note Update Status Test")
    void deadlineNoteUpdateStatusTest() {

        Status updatedStatus = Status.NEW;
        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setStatus(updatedStatus);

        String noteId = deadlineNote.getId().toHexString();
        DeadlineNoteDto updatedNote = (DeadlineNoteDto) notesService.updateNote(ownerId, noteId, patchDeadlineNoteRequest);

        assertNotNull(updatedNote);
        assertEquals(updatedStatus, updatedNote.getStatus());

        assertNotNull(updatedNote.getCreatedAt());
        assertNotNull(updatedNote.getUpdatedAt());
        assertNotNull(updatedNote.getDeadline());

        assertTrue(updatedNote.getUpdatedAt().isAfter(updatedNote.getCreatedAt()));
    }

    @Test
    @DisplayName("Deadline Note Update Valid Deadline Test")
    void deadlineNoteUpdateValidDeadlineTest() {

        Instant updatedDeadline = Instant.now().plus(12, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setDeadline(updatedDeadline);

        String noteId = deadlineNote.getId().toHexString();
        DeadlineNoteDto updatedNote = (DeadlineNoteDto) notesService.updateNote(ownerId, noteId, patchDeadlineNoteRequest);

        assertNotNull(updatedNote);
        assertNotNull(updatedNote.getDeadline());
        assertEquals(updatedDeadline, updatedNote.getDeadline());

        assertNotNull(updatedNote.getCreatedAt());
        assertNotNull(updatedNote.getUpdatedAt());

        assertTrue(updatedNote.getUpdatedAt().isAfter(updatedNote.getCreatedAt()));
    }

    @Test
    @DisplayName("Deadline Note Update Invalid Deadline Test")
    void deadlineNoteUpdateInvalidDeadlineTest() {

        Instant updatedDeadline = Instant.now().minus(5, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setDeadline(updatedDeadline);

        String noteId = deadlineNote.getId().toHexString();
        DeadlineNoteDto updatedNote = (DeadlineNoteDto) notesService.updateNote(ownerId, noteId, patchDeadlineNoteRequest);

        assertNotNull(updatedNote);
        assertNotNull(updatedNote.getDeadline());

        assertNotEquals(updatedDeadline, updatedNote.getDeadline());
        assertEquals(deadline, updatedNote.getDeadline());

        assertNotNull(updatedNote.getCreatedAt());
        assertNotNull(updatedNote.getUpdatedAt());

        assertTrue(updatedNote.getUpdatedAt().isAfter(updatedNote.getCreatedAt()));
    }

    @Test
    @DisplayName("Update Wrong Request")
    void updateWronPatchTest() {

        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();

        String noteId = regularNote.getId().toHexString();
        assertThrows(RuntimeException.class, () -> notesService.updateNote(ownerId, noteId, patchDeadlineNoteRequest));
    }
}
