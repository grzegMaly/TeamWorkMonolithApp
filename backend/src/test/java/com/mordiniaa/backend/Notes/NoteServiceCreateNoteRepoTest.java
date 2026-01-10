package com.mordiniaa.backend.Notes;

import com.mordiniaa.backend.BackendApplication;
import com.mordiniaa.backend.dto.DeadlineNoteDto;
import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.deadline.CreateDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.regular.CreateRegularNoteRequest;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(classes = BackendApplication.class)
public class NoteServiceCreateNoteRepoTest {

    @Autowired
    private NotesServiceImpl notesService;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private NoteMapper noteMapper;

    private final UUID ownerId = UUID.randomUUID();
    private final String title = "My Test Title";
    private final String content = "My Test Content";
    private final Category category = Category.DIARY;
    private final Status status = Status.CANCELED;
    private final Priority priority = Priority.LOW;
    private final Instant deadline = Instant.now().plus(5, ChronoUnit.DAYS);

    @AfterEach
    void clear() {
        notesRepository.deleteAll();
    }

    @Test
    @DisplayName("Create Regular Note Test")
    void createRegularNoteTest() {

        CreateRegularNoteRequest createRegularNoteRequest = new CreateRegularNoteRequest();
        createRegularNoteRequest.setTitle(title);
        createRegularNoteRequest.setContent(content);
        createRegularNoteRequest.setCategory(category);

        RegularNoteDto noteDto = (RegularNoteDto) notesService.createNote(ownerId, createRegularNoteRequest);
        assertNotNull(noteDto);
        assertNotNull(noteDto.getId());
        assertEquals(ownerId, noteDto.getOwnerId());
        assertNotNull(noteDto.getCreatedAt());
        assertNotNull(noteDto.getUpdatedAt());

        assertEquals(title, noteDto.getTitle());
        assertEquals(content, noteDto.getContent());
        assertNotNull(noteDto.getCategory());
        assertEquals(category, noteDto.getCategory());
    }

    @Test
    @DisplayName("Create Deadline Note Test")
    void createDeadlineNoteTest() {

        CreateDeadlineNoteRequest deadlineNoteRequest = new CreateDeadlineNoteRequest();
        deadlineNoteRequest.setTitle(title);
        deadlineNoteRequest.setContent(content);
        deadlineNoteRequest.setStatus(status);
        deadlineNoteRequest.setPriority(priority);
        deadlineNoteRequest.setDeadline(deadline);

        DeadlineNoteDto noteDto = (DeadlineNoteDto) notesService.createNote(ownerId, deadlineNoteRequest);

        assertNotNull(noteDto);
        assertNotNull(noteDto.getId());
        assertEquals(ownerId, noteDto.getOwnerId());
        assertNotNull(noteDto.getCreatedAt());
        assertNotNull(noteDto.getUpdatedAt());
        assertNotNull(noteDto.getDeadline());

        assertEquals(title, noteDto.getTitle());
        assertEquals(content, noteDto.getContent());
        assertNotNull(noteDto.getPriority());
        assertNotNull(noteDto.getStatus());
        assertEquals(status, noteDto.getStatus());
        assertEquals(priority, noteDto.getPriority());
    }
}
