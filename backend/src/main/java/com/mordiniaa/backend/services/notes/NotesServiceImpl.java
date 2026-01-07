package com.mordiniaa.backend.services.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.models.notes.Note;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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
    public Optional<List<NoteDto>> fetchAllNotesForUser(UUID ownerId) {

        List<Note> notes = notesRepository.findAllByOwnerId(ownerId);

        return null;
    }

    @Override
    public Optional<NoteDto> createNote(NoteDto noteDto) {
        return null;
    }

    @Override
    public Optional<NoteDto> updateNote(NoteDto noteDto) {
        return null;
    }

    @Override
    public void deleteNote(String noteId) {
        ObjectId id = new ObjectId(noteId);
    }
}
