package com.mordiniaa.backend.Notes.serviceMock;

import com.mordiniaa.backend.dto.DeadlineNoteDto;
import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.deadline.CreateDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.regular.CreateRegularNoteRequest;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotesServiceCreateNoteMockTest {

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
    private final Priority priority = Priority.HIGH;
    private final Status status = Status.CANCELED;
    private final Instant createdAt = Instant.now().minus(2, ChronoUnit.DAYS);
    private final Instant updatedAt = createdAt;
    private final Instant deadline = Instant.now().plus(3, ChronoUnit.DAYS);

    private CreateRegularNoteRequest createRegularNoteRequest;
    private CreateDeadlineNoteRequest createDeadlineNoteRequest;

    private RegularNote regularNote;
    private DeadlineNote deadlineNote;

    private RegularNote savedRegularNote;
    private DeadlineNote savedDeadlineNote;

    private RegularNoteDto regularNoteDto;
    private DeadlineNoteDto deadlineNoteDto;

    @BeforeEach
    void setup() {

        createRegularNoteRequest = new CreateRegularNoteRequest();
        createRegularNoteRequest.setTitle(baseTitle);
        createRegularNoteRequest.setContent(baseContent);
        createRegularNoteRequest.setCategory(category);

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

        createDeadlineNoteRequest = new CreateDeadlineNoteRequest();
        createDeadlineNoteRequest.setTitle(baseTitle);
        createDeadlineNoteRequest.setContent(baseContent);
        createDeadlineNoteRequest.setStatus(status);
        createDeadlineNoteRequest.setPriority(priority);
        createDeadlineNoteRequest.setDeadline(deadline);

        deadlineNote = new DeadlineNote();
        deadlineNote.setTitle(baseTitle);
        deadlineNote.setContent(baseContent);
        deadlineNote.setArchived(false);
        deadlineNote.setStatus(status);
        deadlineNote.setPriority(priority);
        deadlineNote.setDeadline(deadline);

        savedDeadlineNote = new DeadlineNote();
        savedDeadlineNote.setId(noteId);
        savedDeadlineNote.setOwnerId(ownerId);
        savedDeadlineNote.setTitle(baseTitle);
        savedDeadlineNote.setContent(baseContent);
        savedDeadlineNote.setArchived(false);
        savedDeadlineNote.setStatus(status);
        savedDeadlineNote.setPriority(priority);
        savedDeadlineNote.setDeadline(deadline);
        savedDeadlineNote.setCreatedAt(createdAt);
        savedDeadlineNote.setUpdatedAt(updatedAt);

        deadlineNoteDto = new DeadlineNoteDto();
        deadlineNoteDto.setId(noteId.toHexString());
        deadlineNoteDto.setStatus(status);
        deadlineNoteDto.setPriority(priority);
        deadlineNoteDto.setTitle(baseTitle);
        deadlineNoteDto.setContent(baseContent);
        deadlineNoteDto.setCreatedAt(createdAt);
        deadlineNoteDto.setUpdatedAt(updatedAt);
        deadlineNoteDto.setDeadline(deadline);
        deadlineNoteDto.setOwnerId(ownerId);
    }

    @Test
    @DisplayName("Create Regular Note Test")
    void createRegularNoteTest() {

        when(noteMapper.toModel(createRegularNoteRequest))
                .thenReturn(regularNote);

        when(notesRepository.save(regularNote))
                .thenReturn(savedRegularNote);

        when(noteMapper.toDto(savedRegularNote))
                .thenReturn(regularNoteDto);

        NoteDto noteDto = notesService.createNote(ownerId, createRegularNoteRequest);
        assertEquals(ownerId, noteDto.getOwnerId());
        assertEquals(noteId.toHexString(), noteDto.getId());
        assertNotNull(noteDto.getCreatedAt());
        assertNotNull(noteDto.getUpdatedAt());
    }

    @Test
    @DisplayName("Create Deadline Note Test")
    void createDeadlineNoteTest() {

        when(noteMapper.toModel(createDeadlineNoteRequest))
                .thenReturn(deadlineNote);

        when(notesRepository.save(deadlineNote))
                .thenReturn(savedDeadlineNote);

        when(noteMapper.toDto(savedDeadlineNote))
                .thenReturn(deadlineNoteDto);

        DeadlineNoteDto noteDto = (DeadlineNoteDto) notesService.createNote(ownerId, createDeadlineNoteRequest);
        assertEquals(ownerId, noteDto.getOwnerId());
        assertEquals(noteId.toHexString(), noteDto.getId());
        assertNotNull(noteDto.getDeadline());
        assertEquals(createdAt, noteDto.getCreatedAt());
    }
}
