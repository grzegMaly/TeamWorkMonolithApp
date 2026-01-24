package com.mordiniaa.backend.note.serviceMock;

import com.mordiniaa.backend.dto.note.DeadlineNoteDto;
import com.mordiniaa.backend.dto.note.NoteDto;
import com.mordiniaa.backend.dto.note.RegularNoteDto;
import com.mordiniaa.backend.mappers.note.NoteMapper;
import com.mordiniaa.backend.models.note.Note;
import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotesServiceGetNoteByIdMockTest {

    @InjectMocks
    private NotesServiceImpl notesService;

    @Mock
    private NotesRepository notesRepository;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private MongoTemplate mongoTemplate;

    private static final ObjectId id = ObjectId.get();
    private static final UUID ownerId = UUID.randomUUID();
    private static final Instant now = Instant.now();
    private static final Instant createdAt = now.minus(2, ChronoUnit.DAYS);
    private static final Instant updatedAt = now.minus(1, ChronoUnit.DAYS);
    private static final String title = "My Custom Title";
    private static final String content = "My Custom Content";
    private static final Instant deadline = now.plus(3, ChronoUnit.DAYS);
    private static final Category category = Category.MEETING;
    private static final Priority priority = Priority.LOW;
    private static final Status status = Status.IN_PROGRESS;

    private static RegularNote regularNote;
    private static RegularNoteDto regularNoteDto;
    private static DeadlineNote deadlineNote;
    private static DeadlineNoteDto deadlineNoteDto;

    @BeforeAll
    static void setup() {

        regularNote = new RegularNote();
        regularNote.setId(id);
        regularNote.setArchived(true);
        regularNote.setOwnerId(ownerId);
        regularNote.setContent(content);
        regularNote.setCreatedAt(createdAt);
        regularNote.setUpdatedAt(updatedAt);
        regularNote.setTitle(title);
        regularNote.setCategory(category);

        regularNoteDto = new RegularNoteDto();
        regularNoteDto.setId(id.toHexString());
        regularNoteDto.setContent(content);
        regularNoteDto.setCategory(category);
        regularNoteDto.setTitle(title);
        regularNoteDto.setCreatedAt(createdAt);
        regularNoteDto.setUpdatedAt(updatedAt);
        regularNoteDto.setOwnerId(ownerId);

        deadlineNote = new DeadlineNote();
        deadlineNote.setId(id);
        regularNote.setArchived(true);
        deadlineNote.setOwnerId(ownerId);
        deadlineNote.setTitle(title);
        deadlineNote.setContent(content);
        deadlineNote.setPriority(priority);
        deadlineNote.setStatus(status);
        deadlineNote.setCreatedAt(createdAt);
        deadlineNote.setUpdatedAt(updatedAt);
        deadlineNote.setDeadline(deadline);

        deadlineNoteDto = new DeadlineNoteDto();
        deadlineNoteDto.setId(id.toHexString());
        deadlineNoteDto.setOwnerId(ownerId);
        deadlineNoteDto.setTitle(title);
        deadlineNoteDto.setContent(content);
        deadlineNoteDto.setPriority(priority);
        deadlineNoteDto.setStatus(status);
        deadlineNoteDto.setCreatedAt(createdAt);
        deadlineNoteDto.setUpdatedAt(updatedAt);
        deadlineNoteDto.setDeadline(deadline);
    }

    @Test
    @DisplayName(value = "Get regular note by id valid")
    void getRegNoteByIdValid() {

        when(mongoTemplate.findOne(any(Query.class), eq(Note.class))).thenReturn(regularNote);
        when(noteMapper.toDto(regularNote)).thenReturn(regularNoteDto);

        assertDoesNotThrow(() -> notesService.getNoteById(id.toHexString(), ownerId),
                "Method Should not throw any exception");
        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(id.toHexString(), ownerId);
        assertTrue(noteDtoOpt.isPresent());

        RegularNoteDto receivedNoteDto = (RegularNoteDto) noteDtoOpt.get();
        assertEquals(regularNoteDto.getTitle(), receivedNoteDto.getTitle());
        assertEquals(regularNoteDto.getId(), receivedNoteDto.getId());

        verify(mongoTemplate, times(2)).findOne(any(Query.class), eq(Note.class));
    }

    @Test
    @DisplayName("Get regular note wrong id")
    void getRegNoteByWrongId() {

        ObjectId nextId = ObjectId.get();
        when(mongoTemplate.findOne(any(Query.class), eq(Note.class)))
                .thenReturn(null);

        assertDoesNotThrow(() -> notesService.getNoteById(nextId.toHexString(), ownerId),
                "Should not throw any exception");
        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(nextId.toHexString(), ownerId);
        assertTrue(noteDtoOpt.isEmpty());
        verify(mongoTemplate, times(2)).findOne(any(Query.class), eq(Note.class));
    }

    @Test
    @DisplayName("Get regular note invalid id")
    void getRegNoteByInvalidId() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            sb.append((char) random.nextInt(97, 123));
        }

        assertFalse(ObjectId.isValid(sb.toString()));
        assertThrows(IllegalArgumentException.class, () -> notesService.getNoteById(sb.toString(), ownerId));
    }

    @Test
    @DisplayName("Get regular note wrong owner")
    void getRegNoteInvalidOwner() {

        UUID oId = UUID.randomUUID();
        when(mongoTemplate.findOne(any(Query.class), eq(Note.class)))
                .thenReturn(null);

        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(id.toHexString(), oId);
        assertTrue(noteDtoOpt.isEmpty());
    }

    @Test
    @DisplayName("Get deadline note by id valid")
    void getDeadNoteByIdValid() {

        when(mongoTemplate.findOne(any(Query.class), eq(Note.class)))
                .thenReturn(deadlineNote);
        when(noteMapper.toDto(deadlineNote))
                .thenReturn(deadlineNoteDto);

        assertDoesNotThrow(() -> notesService.getNoteById(id.toHexString(), ownerId),
                "Method Should not throw any exception");
        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(id.toHexString(), ownerId);
        assertTrue(noteDtoOpt.isPresent());

        DeadlineNoteDto receivedNoteDto = (DeadlineNoteDto) noteDtoOpt.get();
        assertEquals(deadlineNoteDto.getTitle(), receivedNoteDto.getTitle());
        assertEquals(deadlineNoteDto.getId(), receivedNoteDto.getId());
        verify(mongoTemplate, times(2)).findOne(any(Query.class), eq(Note.class));
    }

    @Test
    @DisplayName("Get deadline note by wrong id")
    void getDeadNoteByWrongId() {

        ObjectId nextId = ObjectId.get();
        when(mongoTemplate.findOne(any(Query.class), any()))
                .thenReturn(null);

        assertDoesNotThrow(() -> notesService.getNoteById(nextId.toHexString(), ownerId),
                "Should not throw any exception");

        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(nextId.toHexString(), ownerId);
        assertTrue(noteDtoOpt.isEmpty());
        verify(mongoTemplate, times(2)).findOne(any(Query.class), any());
    }

    @Test
    @DisplayName("Get deadline note invalid id")
    void getDeadNoteByInvalidId() {

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            sb.append((char) random.nextInt(97, 123));
        }

        assertFalse(ObjectId.isValid(sb.toString()));
        assertThrows(IllegalArgumentException.class, () -> notesService.getNoteById(sb.toString(), ownerId));
    }

    @Test
    @DisplayName("Get deadline note wrong owner")
    void getDeadNoteWrongOwner() {

        UUID oId = UUID.randomUUID();
        when(mongoTemplate.findOne(any(Query.class), eq(Note.class)))
                .thenReturn(null);

        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(id.toHexString(), oId);
        assertTrue(noteDtoOpt.isEmpty());
    }
}
