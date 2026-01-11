package com.mordiniaa.backend.Notes.serviceMock;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.regular.PatchRegularNoteRequest;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceUpdateNoteMockTest {

    @InjectMocks
    private NotesServiceImpl notesService;

    @Mock
    private NotesRepository notesRepository;

    @Mock
    private NoteMapper noteMapper;

    private final ObjectId noteId = ObjectId.get();
    private final UUID ownerId = UUID.randomUUID();
    private final String baseTitle = "Base Title";
    private final String baseContent = "Base Content";
    private final Category category = Category.DIARY;
    private final Instant createdAt = Instant.now().minus(2, ChronoUnit.DAYS);
    private final Instant updatedAt = createdAt;

    private RegularNote regularNote;

    private RegularNote savedRegularNote;

    private RegularNoteDto regularNoteDto;

    @BeforeEach
    void setup() {

        regularNote = new RegularNote();
        regularNote.setTitle(baseTitle);
        regularNote.setArchived(false);
        regularNote.setContent(baseContent);
        regularNote.setCategory(category);

        savedRegularNote = new RegularNote();
        savedRegularNote.setId(noteId);
        savedRegularNote.setTitle(baseTitle);
        savedRegularNote.setArchived(false);
        savedRegularNote.setContent(baseContent);
        savedRegularNote.setCategory(category);
        savedRegularNote.setCreatedAt(createdAt);
        savedRegularNote.setUpdatedAt(updatedAt);

        regularNoteDto = new RegularNoteDto();
        regularNoteDto.setId(noteId.toString());
        regularNoteDto.setTitle(baseTitle);
        regularNoteDto.setCategory(category);
        regularNoteDto.setContent(baseContent);
        regularNoteDto.setOwnerId(ownerId);
        regularNoteDto.setCreatedAt(createdAt);
        regularNoteDto.setUpdatedAt(updatedAt);
    }

    @Test
    @DisplayName("Patch Regular Note")
    void patchRegularNoteTest() {

        String updatedTitle = "X";
        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setTitle(updatedTitle);

        when(notesRepository.findNoteByIdAndOwnerId(noteId, ownerId))
                .thenReturn(Optional.of(regularNote));

        doAnswer(invocation -> {
            Note note = invocation.getArgument(0);
            PatchRegularNoteRequest req = invocation.getArgument(1);

            note.setTitle(req.getTitle());
            return null;
        }).when(noteMapper).updateNote(any(Note.class), any(PatchRegularNoteRequest.class));

        when(notesRepository.save(any(Note.class)))
                .thenReturn(savedRegularNote);

        RegularNoteDto updatedDto = new RegularNoteDto();
        updatedDto.setId(regularNoteDto.getId());
        updatedDto.setOwnerId(ownerId);
        updatedDto.setTitle(updatedTitle);
        updatedDto.setContent(regularNoteDto.getContent());
        updatedDto.setCategory(regularNoteDto.getCategory());
        updatedDto.setCreatedAt(regularNoteDto.getCreatedAt());
        updatedDto.setUpdatedAt(regularNoteDto.getUpdatedAt());
        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(updatedDto);

        NoteDto noteDto = notesService.updateNote(ownerId, noteId.toHexString(), patchRegularNoteRequest);

        assertNotNull(noteDto);
        assertEquals(ownerId, noteDto.getOwnerId());
        assertEquals(updatedTitle, noteDto.getTitle());
    }

    @Test
    @DisplayName("Patch Regular Note Invalid Note Id Test")
    void patchRegularNoteInvalidIdTest() {

        assertThrows(RuntimeException.class,
                () -> notesService.updateNote(
                        ownerId,
                        "invalid-id",
                        new PatchRegularNoteRequest()
                )
        );
        verifyNoInteractions(notesRepository);
        verifyNoInteractions(noteMapper);
    }
}
