package com.mordiniaa.backend.services.notes;

import com.mordiniaa.backend.config.NotesConstants;
import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import com.mordiniaa.backend.request.note.CreateNoteRequest;
import com.mordiniaa.backend.request.note.PatchNoteRequest;
import com.mordiniaa.backend.utils.PageResult;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final NoteMapper noteMapper;

    @Override
    public Optional<NoteDto> getNoteById(String noteId, UUID ownerId) {

        if (!ObjectId.isValid(noteId)) {
            throw new IllegalArgumentException("Invalid Id");
        }
        return notesRepository.findNoteByIdAndOwnerId(new ObjectId(noteId), ownerId)
                .map(noteMapper::toDto);
    }

    @Override
    public PageResult<List<NoteDto>> fetchAllNotesForUser(UUID ownerId, int pageNumber, int pageSize, String sortOrder, String sortKey, String keyword) {

        if (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")) {
            throw new RuntimeException();
        }

        if (!NotesConstants.ALLOWED_SORTING_KEYS.contains(sortKey)) {
            throw new RuntimeException(); //TODO: Change
        }

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortKey).ascending()
                : Sort.by(sortKey).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Note> page =
                (keyword != null && !keyword.isBlank())
                        ? notesRepository.findAllByOwnerId(
                        ownerId,
                        pageable,
                        TextCriteria.forDefaultLanguage().caseSensitive(false).matching(keyword))
                        : notesRepository.findAllByOwnerId(ownerId, pageable);

        PageResult<List<NoteDto>> result = new PageResult<>();


        result.setData(page.map(noteMapper::toDto).stream().toList());
        result.setUpPage(page);

        return result;
    }

    @Override
    public NoteDto createNote(UUID ownerId, CreateNoteRequest createNoteRequest) {
        Note mappedNote = noteMapper.toModel(createNoteRequest);
        mappedNote.setOwnerId(ownerId);

        Note savedNote = notesRepository.save(mappedNote);
        return noteMapper.toDto(savedNote);
    }

    @Override
    public Optional<NoteDto> updateNote(UUID ownerId, String noteId, PatchNoteRequest patchNoteRequest) {
        return null;
    }

    @Override
    public void deleteNote(UUID ownerId, String noteId) {
        ObjectId id = new ObjectId(noteId);
    }
}
