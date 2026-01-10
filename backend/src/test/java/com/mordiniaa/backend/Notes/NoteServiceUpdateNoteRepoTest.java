package com.mordiniaa.backend.Notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.mappers.notes.dtoMappers.DeadlineNoteDtoMapper;
import com.mordiniaa.backend.mappers.notes.dtoMappers.RegularNoteDtoMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.DeadlineNoteModelMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.RegularNoteModelMapper;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.regular.PatchRegularNoteRequest;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private NoteMapper noteMapper;

    @Autowired
    private NotesServiceImpl notesService;

    private UUID ownerId;

    private final String title = "Title";
    private final String content = "Content";

    private Category category = Category.DIARY;

    private Priority priority = Priority.HIGH;
    private Status status = Status.CANCELED;
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

        deadlineNote = notesRepository.save(DeadlineNote.builder()
                .ownerId(ownerId)
                .title(title)
                .content(content)
                .priority(priority)
                .status(status)
                .deadline(deadline)
                .build()
        );
    }

    @AfterEach
    void clean() {
        notesRepository.deleteAll(List.of(regularNote, deadlineNote));
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

        assertEquals(updatedTitle, updatedNote.getTitle(), "Titles should be the same");
    }
}
