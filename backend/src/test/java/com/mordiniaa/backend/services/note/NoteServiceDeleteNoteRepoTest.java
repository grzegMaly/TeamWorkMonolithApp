package com.mordiniaa.backend.services.note;

import com.mordiniaa.backend.mappers.note.NoteMapper;
import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class NoteServiceDeleteNoteRepoTest {

    @MockitoBean(name = "mongoAuditor")
    AuditorAware<String> mongoAuditor;

    @MockitoBean
    private NoteMapper noteMapper;

    @Autowired
    private NotesServiceImpl notesService;

    @Autowired
    private NotesRepository notesRepository;

    private final UUID ownerId = UUID.randomUUID();

    private RegularNote regularNote;
    private DeadlineNote deadlineNote;

    @BeforeEach
    void setup() {
        when(mongoAuditor.getCurrentAuditor())
                .thenReturn(Optional.of("MONGO_TEST"));

        regularNote = notesRepository.save(RegularNote.builder()
                .ownerId(ownerId)
                .title("Title")
                .content("Content")
                .category(Category.DIARY)
                .build()
        );

        deadlineNote = notesRepository.save(DeadlineNote.builder()
                .ownerId(ownerId)
                .title("Title")
                .content("Content")
                .status(Status.CANCELED)
                .priority(Priority.LOW)
                .deadline(Instant.now().plus(3, ChronoUnit.DAYS))
                .build());
    }

    @AfterEach
    void clear() {
        notesRepository.deleteAll();
    }

    @Test
    @DisplayName("Delete Regular Note Valid Test")
    void deleteRegularNoteValidTest() {

        String noteId = regularNote.getId().toHexString();
        assertDoesNotThrow(() -> notesService.deleteNote(ownerId, noteId));
    }

    @Test
    @DisplayName("Delete Regular Note Invalid Id Test")
    void deleteRegularNoteInvalidIdTest() {

        String noteId = "2f3wqedcw354t435tgwefrw";
        assertThrows(RuntimeException.class, () -> notesService.deleteNote(ownerId, noteId));
    }

    @Test
    @DisplayName("Delete Regular Note Id Not Found Test")
    void deleteRegularNoteIdNotFoundTest() {

        String noteId = ObjectId.get().toHexString();
        assertThrows(RuntimeException.class, () -> notesService.deleteNote(ownerId, noteId));
    }

    @Test
    @DisplayName("Delete Regular Note Owner Not Found Test")
    void deleteRegularNoteOwnerNotFoundTest() {

        String noteId = regularNote.getId().toHexString();
        UUID ownerId = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> notesService.deleteNote(ownerId, noteId));
    }

    @Test
    @DisplayName("Delete Deadline Note Valid Test")
    void deleteDeadlineNoteValidTest() {

        String noteId = deadlineNote.getId().toHexString();
        assertDoesNotThrow(() -> notesService.deleteNote(ownerId, noteId));
    }

    @Test
    @DisplayName("Delete Deadline Note Owner Not Found Test")
    void deleteDeadlineNoteOwnerNotFoundTest() {

        String noteId = deadlineNote.getId().toHexString();
        UUID ownerId = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> notesService.deleteNote(ownerId, noteId));
    }
}
