package com.mordiniaa.backend.services.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.utils.PageResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotesService {
    Optional<NoteDto> getNoteById(String noteId, UUID ownerId);

    PageResult<List<NoteDto>> fetchAllNotesForUser(UUID ownerId, int pageNumber, int pageSize, String sortOrder, String sortKey, String keyword);

    Optional<NoteDto> createNote(UUID ownerId, NoteDto noteDto);

    Optional<NoteDto> updateNote(UUID ownerId, NoteDto noteDto);

    void deleteNote(UUID ownerId, String noteId);
}
