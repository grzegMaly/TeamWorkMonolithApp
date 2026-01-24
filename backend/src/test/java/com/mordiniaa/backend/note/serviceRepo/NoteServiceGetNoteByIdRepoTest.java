package com.mordiniaa.backend.note.serviceRepo;

import com.mordiniaa.backend.dto.note.DeadlineNoteDto;
import com.mordiniaa.backend.dto.note.NoteDto;
import com.mordiniaa.backend.dto.note.RegularNoteDto;
import com.mordiniaa.backend.mappers.note.NoteMapper;
import com.mordiniaa.backend.mappers.note.dtoMappers.DeadlineNoteDtoMapper;
import com.mordiniaa.backend.mappers.note.dtoMappers.RegularNoteDtoMapper;
import com.mordiniaa.backend.mappers.note.modelMappers.DeadlineNoteModelMapper;
import com.mordiniaa.backend.mappers.note.modelMappers.RegularNoteModelMapper;
import com.mordiniaa.backend.models.note.deadline.Priority;
import com.mordiniaa.backend.models.note.deadline.Status;
import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.deadline.CreateDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.regular.CreateRegularNoteRequest;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DataMongoTest
@Import({
        NoteMapper.class,
        RegularNoteDtoMapper.class,
        DeadlineNoteDtoMapper.class,
        RegularNoteModelMapper.class,
        DeadlineNoteModelMapper.class,
        NotesServiceImpl.class
})
public class NoteServiceGetNoteByIdRepoTest {

    @MockitoBean("mongoAuditor")
    private AuditorAware<String> auditorAware;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private NotesServiceImpl notesService;

    private final UUID ownerOneId = UUID.randomUUID();
    private final UUID ownerTwoId = UUID.randomUUID();

    private final List<NoteDto> ownerOneNotes = new ArrayList<>();
    private final List<NoteDto> ownerTwoNotes = new ArrayList<>();

    @BeforeEach
    void setup() {
        when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of("MONGO_TEST"));


        for (int i = 0; i < 2; i++) {
            CreateRegularNoteRequest createRegularNoteRequest = new CreateRegularNoteRequest();
            createRegularNoteRequest.setTitle("Title");
            createRegularNoteRequest.setContent("Content");
            createRegularNoteRequest.setCategory(Category.DIARY);
            RegularNoteDto regularNoteDto = (RegularNoteDto) notesService.createNote(List.of(ownerOneId, ownerTwoId).get(i), createRegularNoteRequest);
            List.of(ownerOneNotes, ownerTwoNotes).get(i).add(regularNoteDto);

            CreateDeadlineNoteRequest createDeadlineNoteRequest = new CreateDeadlineNoteRequest();
            createDeadlineNoteRequest.setTitle("Title");
            createDeadlineNoteRequest.setContent("Content");
            createDeadlineNoteRequest.setPriority(Priority.LOW);
            createDeadlineNoteRequest.setStatus(Status.CANCELED);
            createDeadlineNoteRequest.setDeadline(Instant.now().plus(4, ChronoUnit.DAYS));
            DeadlineNoteDto deadlineNoteDto = (DeadlineNoteDto) notesService.createNote(List.of(ownerOneId, ownerTwoId).get(i), createDeadlineNoteRequest);
            List.of(ownerOneNotes, ownerTwoNotes).get(i).add(deadlineNoteDto);

            assertNotNull(List.of(ownerOneNotes, ownerTwoNotes).get(i).getFirst());
            assertNotNull(List.of(ownerOneNotes, ownerTwoNotes).get(i).getFirst().getId());

            assertNotNull(List.of(ownerOneNotes, ownerTwoNotes).get(i).getLast());
            assertNotNull(List.of(ownerOneNotes, ownerTwoNotes).get(i).getLast().getId());
        }
    }

    @AfterEach
    void clear() {
        notesRepository.deleteAll();
    }

    @Test
    @DisplayName("Get Regular Note By Id Test")
    void getRegularNoteByIdTest() {

        for (List<NoteDto> dtos : List.of(ownerOneNotes, ownerTwoNotes)) {
            Optional<NoteDto> noteDtoOpt =  notesService.getNoteById(
                    dtos.getFirst().getId(),
                    dtos.getFirst().getOwnerId()
            );

            assertTrue(noteDtoOpt.isPresent(), "Dto should be present");

            RegularNoteDto regularNoteDto = (RegularNoteDto) noteDtoOpt.get();
            assertNotNull(regularNoteDto.getId());
            assertNotNull(regularNoteDto.getOwnerId());
            assertNotNull(regularNoteDto.getTitle());
            assertNotNull(regularNoteDto.getCategory());
            assertNotNull(regularNoteDto.getCreatedAt());
            assertNotNull(regularNoteDto.getUpdatedAt());

            assertEquals(dtos.getFirst().getOwnerId(), regularNoteDto.getOwnerId());
        }
    }

    @Test
    @DisplayName("Get Deadline Note By Id Test")
    void getDeadlineNoteByIdTest() {

        for (List<NoteDto> dtos : List.of(ownerOneNotes, ownerTwoNotes)) {
            Optional<NoteDto> noteDtoOpt =  notesService.getNoteById(
                    dtos.getLast().getId(),
                    dtos.getLast().getOwnerId()
            );

            assertTrue(noteDtoOpt.isPresent(), "Dto should be present");

            DeadlineNoteDto deadlineNoteDto = (DeadlineNoteDto) noteDtoOpt.get();
            assertNotNull(deadlineNoteDto.getId());
            assertNotNull(deadlineNoteDto.getOwnerId());
            assertNotNull(deadlineNoteDto.getTitle());
            assertNotNull(deadlineNoteDto.getCreatedAt());
            assertNotNull(deadlineNoteDto.getUpdatedAt());

            assertNotNull(deadlineNoteDto.getStatus());
            assertNotNull(deadlineNoteDto.getPriority());
            assertNotNull(deadlineNoteDto.getDeadline());

            assertEquals(dtos.getLast().getOwnerId(), deadlineNoteDto.getOwnerId());
        }
    }

    @Test
    @DisplayName("Get Note By Id Not Found Test")
    void getNoteByIdNotFoundTest() {

        ObjectId noteId = ObjectId.get();
        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(noteId.toHexString(), ownerOneId);
        assertFalse(noteDtoOpt.isPresent());
    }

    @Test
    @DisplayName("Get Note By Invalid Id Test")
    void getNoteByInvalidIdTest() {

        String noteId = "fawdcewrgerg";
        assertThrows(RuntimeException.class, () -> notesService.getNoteById(noteId, ownerOneId));
    }

    @Test
    @DisplayName("User Not Owner Test")
    void userNotOwnerTest() {

        Optional<NoteDto> noteDtoOpt = notesService.getNoteById(
                ownerOneNotes.getFirst().getId(),
                ownerTwoId
        );

        assertFalse(noteDtoOpt.isPresent());
    }
}
