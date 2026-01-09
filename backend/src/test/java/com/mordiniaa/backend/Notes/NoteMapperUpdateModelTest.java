package com.mordiniaa.backend.Notes;

import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.DeadlineNoteModelMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.RegularNoteModelMapper;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.request.note.deadline.PatchDeadlineNoteRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        NoteMapper.class,
        RegularNoteModelMapper.class,
        DeadlineNoteModelMapper.class
})
public class NoteMapperUpdateModelTest {

    @Autowired
    private NoteMapper noteMapper;

    private final String title = "Title";
    private final String updatedTitle = "Updated Title";
    private final String content = "Content";
    private final String updatedContent = "Updated Content";
    private final Category category = Category.MEETING;
    private final Category updatedCategory = Category.DIARY;
    private final Status status = Status.COMPLETED;
    private final Status updatedStatus = Status.NEW;
    private final Priority priority = Priority.MEDIUM;
    private final Priority updatedPriority = Priority.HIGH;
    private final Instant createdAt = Instant.now().minus(2, ChronoUnit.DAYS);
    private final Instant updatedAt = createdAt.plus(1, ChronoUnit.DAYS);
    private final Instant deadline = Instant.now().plus(3, ChronoUnit.DAYS);
    private final Instant updatedDeadline = Instant.now().plus(5, ChronoUnit.DAYS);

    private RegularNote regularNote;
    private DeadlineNote deadlineNote;

    @BeforeEach
    void setUp() {

        regularNote = new RegularNote();
        regularNote.setTitle(title);
        regularNote.setContent(content);
        regularNote.setCategory(category);
        regularNote.setCreatedAt(createdAt);
        regularNote.setUpdatedAt(updatedAt);

        deadlineNote = new DeadlineNote();
        deadlineNote.setTitle(title);
        deadlineNote.setContent(content);
        deadlineNote.setStatus(status);
        deadlineNote.setPriority(priority);
        deadlineNote.setDeadline(deadline);
        deadlineNote.setCreatedAt(createdAt);
        deadlineNote.setUpdatedAt(updatedAt);
    }

    @Test
    @DisplayName("Patch Regular Note Title")
    void patchRegularNoteTitle() {

        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setTitle(updatedTitle);

        noteMapper.updateNote(regularNote, patchRegularNoteRequest);

        assertEquals(updatedTitle, regularNote.getTitle());

        assertEquals(content, regularNote.getContent());
        assertEquals(category, regularNote.getCategory());
        assertEquals(createdAt, regularNote.getCreatedAt());
        assertEquals(updatedAt, regularNote.getUpdatedAt());
    }

    @Test
    @DisplayName("Patch Regular Note Content")
    void patchRegularNoteContentTest() {

        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setContent(updatedContent);

        noteMapper.updateNote(regularNote, patchRegularNoteRequest);

        assertEquals(updatedContent, regularNote.getContent());

        assertEquals(title, regularNote.getTitle());
        assertEquals(category, regularNote.getCategory());
        assertEquals(createdAt, regularNote.getCreatedAt());
        assertEquals(updatedAt, regularNote.getUpdatedAt());
    }

    @Test
    @DisplayName("Patch Regular Note Category")
    void patchRegularNoteCategoryTest() {

        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setCategory(updatedCategory);

        noteMapper.updateNote(regularNote, patchRegularNoteRequest);

        assertEquals(updatedCategory, regularNote.getCategory());

        assertEquals(title, regularNote.getTitle());
        assertEquals(content, regularNote.getContent());
        assertEquals(createdAt, regularNote.getCreatedAt());
        assertEquals(updatedAt, regularNote.getUpdatedAt());
    }

    @Test
    @DisplayName("Patch Regular Note All")
    void patchRegularNoteAllTest() {

        PatchRegularNoteRequest patchRegularNoteRequest = new PatchRegularNoteRequest();
        patchRegularNoteRequest.setTitle(updatedTitle);
        patchRegularNoteRequest.setContent(updatedContent);
        patchRegularNoteRequest.setCategory(updatedCategory);

        noteMapper.updateNote(regularNote, patchRegularNoteRequest);

        assertEquals(updatedTitle, regularNote.getTitle());
        assertEquals(updatedCategory, regularNote.getCategory());
        assertEquals(updatedContent, regularNote.getContent());

        assertEquals(createdAt, regularNote.getCreatedAt());
        assertEquals(updatedAt, regularNote.getUpdatedAt());
    }

    @Test
    @DisplayName("Patch Deadline Note Title")
    void patchDeadlineNoteTitle() {

        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setTitle(updatedTitle);

        noteMapper.updateNote(deadlineNote, patchDeadlineNoteRequest);

        assertEquals(updatedTitle, deadlineNote.getTitle());

        assertEquals(content, deadlineNote.getContent());
        assertEquals(status, deadlineNote.getStatus());
        assertEquals(priority, deadlineNote.getPriority());
        assertEquals(createdAt, deadlineNote.getCreatedAt());
        assertEquals(updatedAt, deadlineNote.getUpdatedAt());
        assertEquals(deadline, deadlineNote.getDeadline());
    }

    @Test
    @DisplayName("Patch Deadline Note Content")
    void DeadlineNoteContentTest() {

        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setContent(updatedContent);

        noteMapper.updateNote(deadlineNote, patchDeadlineNoteRequest);

        assertEquals(updatedContent, deadlineNote.getContent());

        assertEquals(title, deadlineNote.getTitle());
        assertEquals(status, deadlineNote.getStatus());
        assertEquals(priority, deadlineNote.getPriority());
        assertEquals(createdAt, deadlineNote.getCreatedAt());
        assertEquals(updatedAt, deadlineNote.getUpdatedAt());
        assertEquals(deadline, deadlineNote.getDeadline());
    }

    @Test
    @DisplayName("Patch Deadline Note Priority")
    void DeadlineNotePriorityTest() {

        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setPriority(updatedPriority);

        noteMapper.updateNote(deadlineNote, patchDeadlineNoteRequest);

        assertEquals(updatedPriority, deadlineNote.getPriority());

        assertEquals(title, deadlineNote.getTitle());
        assertEquals(content, deadlineNote.getContent());
        assertEquals(status, deadlineNote.getStatus());
        assertEquals(createdAt, deadlineNote.getCreatedAt());
        assertEquals(updatedAt, deadlineNote.getUpdatedAt());
        assertEquals(deadline, deadlineNote.getDeadline());
    }

    @Test
    @DisplayName("Patch Deadline Note Status")
    void DeadlineNoteStatusTest() {

        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setStatus(updatedStatus);

        noteMapper.updateNote(deadlineNote, patchDeadlineNoteRequest);

        assertEquals(updatedStatus, deadlineNote.getStatus());

        assertEquals(title, deadlineNote.getTitle());
        assertEquals(content, deadlineNote.getContent());
        assertEquals(priority, deadlineNote.getPriority());
        assertEquals(createdAt, deadlineNote.getCreatedAt());
        assertEquals(updatedAt, deadlineNote.getUpdatedAt());
        assertEquals(deadline, deadlineNote.getDeadline());
    }

    @Test
    @DisplayName("Patch Deadline Note Deadline")
    void DeadlineNoteDeadlineTest() {

        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setDeadline(updatedDeadline);

        noteMapper.updateNote(deadlineNote, patchDeadlineNoteRequest);

        assertEquals(updatedDeadline, deadlineNote.getDeadline());

        assertEquals(title, deadlineNote.getTitle());
        assertEquals(content, deadlineNote.getContent());
        assertEquals(priority, deadlineNote.getPriority());
        assertEquals(status, deadlineNote.getStatus());
        assertEquals(createdAt, deadlineNote.getCreatedAt());
        assertEquals(updatedAt, deadlineNote.getUpdatedAt());
    }

    @Test
    @DisplayName("Patch Deadline Note All")
    void DeadlineNoteAllTest() {

        PatchDeadlineNoteRequest patchDeadlineNoteRequest = new PatchDeadlineNoteRequest();
        patchDeadlineNoteRequest.setTitle(updatedTitle);
        patchDeadlineNoteRequest.setContent(updatedContent);
        patchDeadlineNoteRequest.setPriority(updatedPriority);
        patchDeadlineNoteRequest.setStatus(updatedStatus);
        patchDeadlineNoteRequest.setDeadline(updatedDeadline);

        noteMapper.updateNote(deadlineNote, patchDeadlineNoteRequest);

        assertEquals(updatedTitle, deadlineNote.getTitle());
        assertEquals(updatedContent, deadlineNote.getContent());
        assertEquals(updatedPriority, deadlineNote.getPriority());
        assertEquals(updatedStatus, deadlineNote.getStatus());
        assertEquals(updatedDeadline, deadlineNote.getDeadline());

        assertEquals(createdAt, deadlineNote.getCreatedAt());
        assertEquals(updatedAt, deadlineNote.getUpdatedAt());
    }
}
