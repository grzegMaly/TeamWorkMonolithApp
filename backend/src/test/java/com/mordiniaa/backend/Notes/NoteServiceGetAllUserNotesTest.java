package com.mordiniaa.backend.Notes;

import com.mordiniaa.backend.BackendApplication;
import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.models.notes.deadline.Priority;
import com.mordiniaa.backend.models.notes.deadline.Status;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.payload.PageMeta;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import com.mordiniaa.backend.utils.PageResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = BackendApplication.class)
public class NoteServiceGetAllUserNotesTest {

    @Autowired
    private NotesServiceImpl notesService;

    @Autowired
    private NotesRepository notesRepository;

    private static UUID ownerOneId;
    private static UUID ownerTwoId;

    @BeforeEach
    void setup() {

        Random random = new Random();
        List<Note> storedNotes = new ArrayList<>();

        ownerOneId = UUID.randomUUID();
        ownerTwoId = UUID.randomUUID();
        String baseTitle = "%s Base Title";
        String baseContent = "%s Base Content";
        Instant baseCreatedAt = Instant.now().minus(10, ChronoUnit.DAYS);
        Instant baseUpdatedAt = Instant.now().minus(5, ChronoUnit.DAYS);

        for (UUID ownerId : List.of(ownerOneId, ownerTwoId)) {
            for (int i = 0; i < 30; i++) {
                Note note;
                String formattedTitle = baseTitle.formatted(i < 10 ? "0" + i : i);
                String formattedContent = baseContent.formatted(i < 10 ? "0" + i : i);

                if (random.nextBoolean()) {
                    note = new RegularNote();
                    note.setOwnerId(ownerId);
                    note.setTitle(formattedTitle);
                    note.setContent(formattedContent);
                    note.setArchived(random.nextBoolean());
                    ((RegularNote) note).setCategory(Category.values()[random.nextInt(Category.values().length)]);
                    note.setCreatedAt(baseCreatedAt);
                    note.setUpdatedAt(baseUpdatedAt);
                    storedNotes.add(note);
                } else {
                    note = new DeadlineNote();

                    Instant deadline = Instant.now().plus(random.nextInt(1, 10), ChronoUnit.DAYS);

                    note.setOwnerId(ownerId);
                    note.setTitle(formattedTitle);
                    note.setContent(formattedContent);
                    note.setArchived(random.nextBoolean());
                    ((DeadlineNote) note).setPriority(Priority.values()[random.nextInt(Priority.values().length)]);
                    ((DeadlineNote) note).setStatus(Status.values()[random.nextInt(Status.values().length)]);
                    note.setCreatedAt(baseCreatedAt);
                    note.setUpdatedAt(baseUpdatedAt);
                    ((DeadlineNote) note).setDeadline(deadline);
                    storedNotes.add(note);
                }
            }
        }
        notesRepository.saveAll(storedNotes);
    }

    @AfterEach
    void clear() {
        notesRepository.deleteAll();
    }
}
