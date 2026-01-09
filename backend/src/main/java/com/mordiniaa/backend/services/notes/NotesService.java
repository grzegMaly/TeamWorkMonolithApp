package com.mordiniaa.backend.services.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.request.note.CreateNoteRequest;
import com.mordiniaa.backend.utils.PageResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotesService {
    Optional<NoteDto> getNoteById(String noteId, UUID ownerId);

    PageResult<List<NoteDto>> fetchAllNotesForUser(UUID ownerId, int pageNumber, int pageSize, String sortOrder, String sortKey, String keyword);

    NoteDto createNote(UUID ownerId, CreateNoteRequest createNoteRequest);

    Optional<NoteDto> updateNote(UUID ownerId, NoteDto noteDto);

    void deleteNote(UUID ownerId, String noteId);
}
