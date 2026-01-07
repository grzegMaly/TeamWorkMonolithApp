package com.mordiniaa.backend.services.notes;

import com.mordiniaa.backend.dto.NoteDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotesService {
    Optional<NoteDto> getNoteById(String noteId, UUID ownerId);

    Optional<List<NoteDto>> fetchAllNotesForUser(UUID ownerId);

    Optional<NoteDto> createNote(NoteDto noteDto);

    Optional<NoteDto> updateNote(NoteDto noteDto);

    void deleteNote(String noteId);
}
