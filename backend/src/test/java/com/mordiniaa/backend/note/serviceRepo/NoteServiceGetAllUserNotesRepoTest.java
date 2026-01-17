package com.mordiniaa.backend.note.serviceRepo;

import com.mordiniaa.backend.BackendApplication;
import com.mordiniaa.backend.dto.note.NoteDto;
import com.mordiniaa.backend.models.note.Note;
import com.mordiniaa.backend.models.note.deadline.DeadlineNote;
import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.models.note.regular.RegularNote;
import com.mordiniaa.backend.payload.PageMeta;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.notes.NotesServiceImpl;
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
public class NoteServiceGetAllUserNotesRepoTest {

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
                    note.setArchived(false);
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
                    note.setArchived(false);
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

    @Test
    @DisplayName("Pagination Test")
    void getAllNotesForUser() {

        PageResult<List<NoteDto>> pageResult = notesService.fetchAllNotesForUser(ownerOneId, 0, 5, "asc", "updatedAt", null);

        PageMeta meta = pageResult.getPageMeta();
        assertEquals(6, meta.getTotalPages());
        assertFalse(meta.isLastPage());

        pageResult = notesService.fetchAllNotesForUser(ownerOneId, 5, 5, "asc", "updatedAt", null);
        meta = pageResult.getPageMeta();
        assertEquals(5, meta.getSize());
        assertEquals(5, meta.getPage());
        assertTrue(meta.isLastPage());

        pageResult = notesService.fetchAllNotesForUser(ownerOneId, 0, 25, "asc", "updatedAt", null);
        meta = pageResult.getPageMeta();
        assertEquals(25, meta.getSize());
        assertEquals(0, meta.getPage());
        assertEquals(30, meta.getTotalItems());
        assertFalse(meta.isLastPage());
    }

    @Test
    @DisplayName("Title sort test")
    void titleSortTest() {
        PageResult<List<NoteDto>> pageResult = notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "asc", "title", null);
        List<NoteDto> data = pageResult.getData();
        List<Integer> numbers = data.stream()
                .map(NoteDto::getTitle)
                .map(title -> Integer.valueOf(title.split(" ")[0]))
                .toList();

        for (int i = 0; i < numbers.size() - 1; i++) {
            assertTrue(numbers.get(i) < numbers.get(i + 1));
        }

        pageResult = notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "desc", "title", null);
        data = pageResult.getData();
        data.forEach(dto -> System.out.println(dto.getTitle()));
        numbers = data.stream()
                .map(NoteDto::getTitle)
                .map(title -> Integer.valueOf(title.split(" ")[0]))
                .toList();

        for (int i = 0; i < numbers.size() - 1; i++) {
            assertTrue(numbers.get(i) > numbers.get(i + 1));
        }
    }

    @Test
    @DisplayName("Test Text Criteria")
    void textCriteriaTest() {
        PageResult<List<NoteDto>> pageResult = notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "asc", "updatedAt", "03");
        List<NoteDto> data = pageResult.getData();

        assertEquals(1, data.size());
    }

    @Test
    @DisplayName("Notes don't equal")
    void notEqualTest() {
        PageResult<List<NoteDto>> firstData = notesService.fetchAllNotesForUser(ownerOneId, 0, 1, "asc", "updatedAt", null);
        PageResult<List<NoteDto>> secondData = notesService.fetchAllNotesForUser(ownerTwoId, 0, 1, "asc", "updatedAt", null);

        NoteDto firstDto = firstData.getData().getFirst();
        NoteDto secondDto = secondData.getData().getFirst();
        assertNotEquals(firstDto, secondDto);
    }

    @Test
    @DisplayName("Invalid sorting order")
    void invalidSortOrderTest() {
        assertDoesNotThrow(() -> notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "asc", "title", null),
                "Should not throw exception for asc");
        assertDoesNotThrow(() -> notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "desc", "title", null),
                "Should not throw exception for desc");

        assertThrows(RuntimeException.class,
                () -> notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "sdfe", "title", null),
                "Should throw exception for asc");
        assertThrows(RuntimeException.class,
                () -> notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "desdversc", "title", null),
                "Should throw exception for desc");
    }

    @Test
    @DisplayName("Empty Result")
    void emptyResultTest() {

        assertFalse(notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "asc", "updatedAt", null).getData().isEmpty());
        notesRepository.deleteAllByOwnerId(ownerOneId);
        assertTrue(notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "asc", "updatedAt", null).getData().isEmpty());
        assertFalse(notesService.fetchAllNotesForUser(ownerTwoId, 0, 10, "asc", "updatedAt", null).getData().isEmpty());
        notesRepository.deleteAllByOwnerId(ownerTwoId);
        assertTrue(notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "asc", "updatedAt", null).getData().isEmpty());
    }

    @Test
    @DisplayName("Page out of range test")
    void pageOutOfRangeTest() {

        PageResult<List<NoteDto>> result = notesService.fetchAllNotesForUser(ownerOneId, 999, 10, "asc", "updatedAt", null);
        assertTrue(result.getData().isEmpty());
        assertTrue(result.getPageMeta().isLastPage());
    }

    @Test
    @DisplayName("Sort Key Not Allowed")
    void sortKeyNotAllowedTest() {
        assertThrows(Exception.class, () -> notesService.fetchAllNotesForUser(ownerOneId, 0, 10, "asc", "category", null));
    }
}
