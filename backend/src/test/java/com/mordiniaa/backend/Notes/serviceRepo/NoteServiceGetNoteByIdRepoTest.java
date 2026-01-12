package com.mordiniaa.backend.Notes.serviceRepo;

import com.mordiniaa.backend.dto.DeadlineNoteDto;
import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.dto.RegularNoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.mappers.notes.dtoMappers.DeadlineNoteDtoMapper;
import com.mordiniaa.backend.mappers.notes.dtoMappers.RegularNoteDtoMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.DeadlineNoteModelMapper;
import com.mordiniaa.backend.mappers.notes.modelMappers.RegularNoteModelMapper;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.deadline.CreateDeadlineNoteRequest;
import com.mordiniaa.backend.request.note.regular.CreateRegularNoteRequest;
import com.mordiniaa.backend.services.notes.NotesServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
