package com.mordiniaa.backend.note.mapper;

import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.DeadlineNoteModelMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.RegularNoteModelMapper;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.request.note.deadline.CreateDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.regular.CreateRegularNoteRequest;
import com.mordiniaa.backend.request.note.regular.PatchRegularNoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        NoteMapper.class,
        RegularNoteModelMapper.class,
        DeadlineNoteModelMapper.class
})
public class NoteMapperToModelTest {

    @Autowired
    private NoteMapper noteMapper;

    private CreateRegularNoteRequest createRegularNoteRequest;
    private CreateDeadlineNoteRequest createDeadlineNoteRequest;

    private final String title = "My Custom Title";
    private final String content = "My Custom Content";
    private final Category category = Category.DIARY;
    private final Status status = Status.CANCELED;
    private final Priority priority = Priority.LOW;
    private final Instant deadline = Instant.now().plus(4, ChronoUnit.DAYS);

    @BeforeEach
    void setup() {

        createRegularNoteRequest = new CreateRegularNoteRequest();
        createRegularNoteRequest.setTitle(title);
        createRegularNoteRequest.setContent(content);
        createRegularNoteRequest.setCategory(category);

        createDeadlineNoteRequest = new CreateDeadlineNoteRequest();
        createDeadlineNoteRequest.setTitle(title);
        createDeadlineNoteRequest.setContent(content);
        createDeadlineNoteRequest.setStatus(status);
        createDeadlineNoteRequest.setPriority(priority);
        createDeadlineNoteRequest.setDeadline(deadline);
    }

    @Test
    @DisplayName("Regular Note Model Test")
    void regularNoteModelTest() {

        RegularNote regularNote = (RegularNote) noteMapper.toModel(createRegularNoteRequest);

        assertNotNull(regularNote.getTitle());
        assertNotNull(regularNote.getContent());
        assertNotNull(regularNote.getCategory());

        assertFalse(regularNote.isArchived());

        assertEquals(title, regularNote.getTitle());
        assertEquals(content, regularNote.getContent());
        assertEquals(category, regularNote.getCategory());
    }

    @Test
    @DisplayName("Regular Note Model Not Equals Test")
    void regularNoteModelNotEqualsTest() {

        RegularNote regularNote = (RegularNote) noteMapper.toModel(createRegularNoteRequest);

        assertNotNull(regularNote.getTitle());
        assertNotNull(regularNote.getContent());
        assertNotNull(regularNote.getCategory());

        assertNotEquals("Custom Title", regularNote.getTitle());
        assertNotEquals("Custom Content", regularNote.getContent());
        assertNotEquals(Category.RECEIPT, regularNote.getCategory());
    }

    @Test
    @DisplayName("Deadline Note Model Test")
    void deadlineNoteModelTest() {

        DeadlineNote deadlineNote = (DeadlineNote) noteMapper.toModel(createDeadlineNoteRequest);

        assertNotNull(deadlineNote.getTitle());
        assertNotNull(deadlineNote.getContent());
        assertNotNull(deadlineNote.getStatus());
        assertNotNull(deadlineNote.getPriority());
        assertNotNull(deadlineNote.getDeadline());

        assertFalse(deadlineNote.isArchived());

        assertEquals(title, deadlineNote.getTitle());
        assertEquals(content, deadlineNote.getContent());
        assertEquals(status, deadlineNote.getStatus());
        assertEquals(priority, deadlineNote.getPriority());
        assertEquals(deadline, deadlineNote.getDeadline());
    }

    @Test
    @DisplayName("Deadline Note Model Not Equals Test")
    void deadlineNoteModelNotEqualsTest() {

        DeadlineNote deadlineNote = (DeadlineNote) noteMapper.toModel(createDeadlineNoteRequest);

        assertNotNull(deadlineNote.getTitle());
        assertNotNull(deadlineNote.getContent());
        assertNotNull(deadlineNote.getStatus());
        assertNotNull(deadlineNote.getPriority());
        assertNotNull(deadlineNote.getDeadline());

        assertNotEquals("Custom Title", deadlineNote.getTitle());
        assertNotEquals("Custom Content", deadlineNote.getContent());
        assertNotEquals(Status.IN_PROGRESS, deadlineNote.getStatus());
        assertNotEquals(Priority.HIGH, deadlineNote.getPriority());
        assertNotEquals(Instant.now().plus(1, ChronoUnit.DAYS), deadlineNote.getDeadline());
    }

    @Test
    @DisplayName("Unsupported request type throws exception")
    void unsupportedRequestTypeTest() {

        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setTitle("X");

        assertThrows(RuntimeException.class, () -> noteMapper.toModel(patchRegularNoteRequest));
    }
}
