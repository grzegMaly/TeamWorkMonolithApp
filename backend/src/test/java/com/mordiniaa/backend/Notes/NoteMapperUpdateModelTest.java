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

    private String title = "Title";
    private String updatedTitle = "Updated Title";
    private String content = "Content";
    private String updatedContent = "Updated Content";
    private Category category = Category.MEETING;
    private Category updatedCategory = Category.DIARY;
    private Status status = Status.COMPLETED;
    private Status updatedStatus = Status.NEW;
    private Priority priority = Priority.MEDIUM;
    private Priority updatedPriority = Priority.HIGH;
    private Instant createdAt = Instant.now().minus(2, ChronoUnit.DAYS);
    private Instant updatedAt = createdAt.plus(1, ChronoUnit.DAYS);
    private Instant deadline = Instant.now().plus(3, ChronoUnit.DAYS);

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
}
