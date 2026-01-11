package com.mordiniaa.backend.Notes.serviceRepo.archived;

import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.models.notes.deadline.DeadlineNote;
import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.models.notes.regular.RegularNote;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.services.notes.ArchivedNotesServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DataMongoTest
@Import({
        NoteMapper.class,
        ArchivedNotesServiceImpl.class
})
public class ArchivedNoteServiceGetAllUserNotesRepoTest {

    @MockitoBean(name = "mongoAuditor")
    private AuditorAware<String> mongoAuditor;

    @Autowired
    private ArchivedNotesServiceImpl archivedNotesService;

    @Autowired
    private NotesRepository notesRepository;

    private final UUID ownerIdOne = UUID.randomUUID();
    private final UUID ownerIdTwo = UUID.randomUUID();

    @BeforeEach
    void setup() {
        when(mongoAuditor.getCurrentAuditor())
                .thenReturn(Optional.of("MONGO_TEST"));

        for (int i = 0; i < 2; i++) {

            boolean archived = i % 2 == 0;
            for (int j = 0; j < 13; j++) {

                RegularNote regularNote = getRegularNote(archived);
                regularNote.setOwnerId(ownerIdOne);
                notesRepository.save(regularNote);

                regularNote = getRegularNote(archived);
                regularNote.setOwnerId(ownerIdTwo);
                notesRepository.save(regularNote);

                DeadlineNote deadlineNote = getDeadlineNote(archived);
                deadlineNote.setOwnerId(ownerIdOne);
                notesRepository.save(deadlineNote);

                deadlineNote = getDeadlineNote(archived);
                deadlineNote.setOwnerId(ownerIdTwo);
                notesRepository.save(deadlineNote);
            }
        }
    }

    private RegularNote getRegularNote(boolean archived) {
        RegularNote regularNote = new RegularNote();
        regularNote.setArchived(archived);
        regularNote.setCategory(Category.DIARY);
        regularNote.setTitle("Title");
        regularNote.setContent("Content");

        return regularNote;
    }

    private DeadlineNote getDeadlineNote(boolean archived) {
        DeadlineNote deadlineNote = new DeadlineNote();
        deadlineNote.setArchived(archived);
        deadlineNote.setTitle("Title");
        deadlineNote.setContent("Content");

        return deadlineNote;
    }

    @AfterEach
    void clear() {
        notesRepository.deleteAll();
    }

    @Test
    void test() {

    }
}
