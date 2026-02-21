package com.mordiniaa.backend.mappers.note;

import com.mordiniaa.backend.dto.note.DeadlineNoteDto;
import com.mordiniaa.backend.dto.note.RegularNoteDto;
import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("test")
public class NotesMapperToDtoTest {

    @Autowired
    private NoteMapper noteMapper;

    private RegularNote regularNote;
    private RegularNoteDto regularNoteDto;
    private DeadlineNote deadlineNote;
    private DeadlineNoteDto deadlineNoteDto;

    @BeforeEach
    void setup() {

        UUID ownerId = UUID.randomUUID();
        ObjectId noteId = new ObjectId("66b23e3e5d2b8c6a9cfa3f12");
        Instant createdAt = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant updatedAt = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant deadline = Instant.now().plus(4, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        String content = "My Custom Testing Content";

        regularNote = new RegularNote();
        regularNote.setId(noteId);
        regularNote.setCategory(Category.DIARY);
        regularNote.setArchived(false);
        regularNote.setTitle("Title123");
        regularNote.setOwnerId(ownerId);
        regularNote.setContent(content);
        regularNote.setCreatedAt(createdAt);
        regularNote.setUpdatedAt(updatedAt);

        regularNoteDto = new RegularNoteDto();
        regularNoteDto.setCategory(Category.DIARY);
        regularNoteDto.setId(noteId.toHexString());
        regularNoteDto.setTitle("Title123");
        regularNoteDto.setContent(content);
        regularNoteDto.setOwnerId(ownerId);
        regularNoteDto.setCreatedAt(createdAt);
        regularNoteDto.setUpdatedAt(updatedAt);

        deadlineNote = new DeadlineNote();
        deadlineNote.setId(noteId);
        deadlineNote.setDeadline(deadline);
        deadlineNote.setPriority(Priority.HIGH);
        deadlineNote.setStatus(Status.CANCELED);
        deadlineNote.setArchived(false);
        deadlineNote.setTitle("Title123");
        deadlineNote.setOwnerId(ownerId);
        deadlineNote.setContent(content);
        deadlineNote.setCreatedAt(createdAt);
        deadlineNote.setUpdatedAt(updatedAt);

        deadlineNoteDto = new DeadlineNoteDto();
        deadlineNoteDto.setId(noteId.toHexString());
        deadlineNoteDto.setDeadline(deadline);
        deadlineNoteDto.setPriority(Priority.HIGH);
        deadlineNoteDto.setStatus(Status.CANCELED);
        deadlineNoteDto.setTitle("Title123");
        deadlineNoteDto.setContent(content);
        deadlineNoteDto.setOwnerId(ownerId);
        deadlineNoteDto.setCreatedAt(createdAt);
        deadlineNoteDto.setUpdatedAt(updatedAt);
    }

    @Test
    void regularNoteToNoteDtoTest() {

        RegularNoteDto mappedDto = (RegularNoteDto) noteMapper.toDto(regularNote);

        assertEquals(regularNoteDto.getId(), mappedDto.getId(), "Id should be the same");
        assertEquals(regularNoteDto.getCategory(), mappedDto.getCategory(), "Category should be the same");
        assertEquals(regularNoteDto.getContent(), mappedDto.getContent(), "Content should be the same");
        assertEquals(regularNoteDto.getTitle(), mappedDto.getTitle(), "Title should be the same");
        assertEquals(regularNoteDto.getCreatedAt(), mappedDto.getCreatedAt(), "CreatedAt should be the same");
        assertEquals(regularNoteDto.getUpdatedAt(), mappedDto.getUpdatedAt(), "UpdatedAt should be the same");
        assertEquals(regularNoteDto.getOwnerId(), mappedDto.getOwnerId(), "Owner Id should be the same");
    }

    @Test
    void regularNoteToNoteDtoDoesNotEqualsTest() {

        regularNoteDto.setCategory(Category.MEETING);
        regularNoteDto.setTitle("New Title");
        regularNoteDto.setCreatedAt(regularNoteDto.getCreatedAt().minus(1, ChronoUnit.DAYS));

        RegularNoteDto mappedDto = (RegularNoteDto) noteMapper.toDto(regularNote);

        assertEquals(regularNoteDto.getId(), mappedDto.getId(), "Id should be the same");
        assertNotEquals(regularNoteDto.getCategory(), mappedDto.getCategory(), "Category should be not the same");
        assertEquals(regularNoteDto.getContent(), mappedDto.getContent(), "Content should be the same");
        assertNotEquals(regularNoteDto.getTitle(), mappedDto.getTitle(), "Title should not be the same");
        assertNotEquals(regularNoteDto.getCreatedAt(), mappedDto.getCreatedAt(), "CreatedAt should not be the same");
        assertEquals(regularNoteDto.getUpdatedAt(), mappedDto.getUpdatedAt(), "UpdatedAt should be the same");
        assertEquals(regularNoteDto.getOwnerId(), mappedDto.getOwnerId(), "Owner Id should be the same");
    }

    @Test
    void deadlineNoteToNoteDtoTest() {

        DeadlineNoteDto mappedDto = (DeadlineNoteDto) noteMapper.toDto(deadlineNote);

        assertEquals(deadlineNoteDto.getId(), mappedDto.getId(), "Id should be the same");
        assertEquals(deadlineNoteDto.getContent(), mappedDto.getContent(), "Content should be the same");
        assertEquals(deadlineNoteDto.getTitle(), mappedDto.getTitle(), "Title should be the same");
        assertEquals(deadlineNoteDto.getCreatedAt(), mappedDto.getCreatedAt(), "CreatedAt should be the same");
        assertEquals(deadlineNoteDto.getUpdatedAt(), mappedDto.getUpdatedAt(), "UpdatedAt should be the same");
        assertEquals(deadlineNoteDto.getOwnerId(), mappedDto.getOwnerId(), "Owner Id should be the same");
        assertEquals(deadlineNoteDto.getDeadline(), mappedDto.getDeadline(), "Deadline should be the same");
        assertEquals(deadlineNoteDto.getPriority(), mappedDto.getPriority(), "Priority should be the same");
        assertEquals(deadlineNoteDto.getStatus(), mappedDto.getStatus(), "Status should be the same");
    }

    @Test
    void deadlineNoteToNoteDtoDoesNotEqualsTest() {

        deadlineNoteDto.setPriority(Priority.LOW);
        deadlineNoteDto.setTitle("My Custom Title");
        deadlineNoteDto.setUpdatedAt(regularNoteDto.getUpdatedAt().minus(1, ChronoUnit.DAYS));

        DeadlineNoteDto mappedDto = (DeadlineNoteDto) noteMapper.toDto(deadlineNote);

        assertEquals(deadlineNoteDto.getId(), mappedDto.getId(), "Id should be the same");
        assertEquals(deadlineNoteDto.getContent(), mappedDto.getContent(), "Content should be the same");
        assertNotEquals(deadlineNoteDto.getTitle(), mappedDto.getTitle(), "Title should not be the same");
        assertEquals(deadlineNoteDto.getCreatedAt(), mappedDto.getCreatedAt(), "CreatedAt should be the same");
        assertNotEquals(deadlineNoteDto.getUpdatedAt(), mappedDto.getUpdatedAt(), "UpdatedAt should not be the same");
        assertEquals(deadlineNoteDto.getOwnerId(), mappedDto.getOwnerId(), "Owner Id should be the same");
        assertEquals(deadlineNoteDto.getDeadline(), mappedDto.getDeadline(), "Deadline should be the same");
        assertNotEquals(deadlineNoteDto.getPriority(), mappedDto.getPriority(), "Priority should not be the same");
        assertEquals(deadlineNoteDto.getStatus(), mappedDto.getStatus(), "Status should be the same");
    }
}
