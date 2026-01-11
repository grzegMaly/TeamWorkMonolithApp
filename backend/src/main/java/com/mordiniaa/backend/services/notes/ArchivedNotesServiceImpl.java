package com.mordiniaa.backend.services.notes;

import com.mongodb.client.result.UpdateResult;
import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.utils.PageResult;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchivedNotesServiceImpl implements ArchivedNotesService {

    private final NotesRepository notesRepository;
    private final NoteMapper noteMapper;

    @Override
    public PageResult<List<NoteDto>> fetchAllArchivedNotes(UUID ownerId, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Note> page = notesRepository.findAllByOwnerIdAndArchived(ownerId, true, pageable);

        PageResult<List<NoteDto>> result = new PageResult<>();

        result.setData(page.map(noteMapper::toDto).stream().toList());
        result.setUpPage(page);

        return result;
    }

    @Override
    public void switchArchivedNoteForUser(UUID ownerId, String noteId) {

        if (!ObjectId.isValid(noteId)) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        ObjectId nId = new ObjectId(noteId);
        UpdateResult result = notesRepository.changeArchivedStatusForNote(ownerId, nId);
        if (result.getModifiedCount() != 1) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }
    }
}
