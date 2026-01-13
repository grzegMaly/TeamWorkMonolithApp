package com.mordiniaa.backend.services.notes.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.request.note.CreateNoteRequest;
import com.mordiniaa.backend.request.note.PatchNoteRequest;
import com.mordiniaa.backend.utils.PageResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotesService {
    Optional<NoteDto> getNoteById(String noteId, UUID ownerId);

    PageResult<List<NoteDto>> fetchAllNotesForUser(UUID ownerId, int pageNumber, int pageSize, String sortOrder, String sortKey, String keyword);

    NoteDto createNote(UUID ownerId, CreateNoteRequest createNoteRequest);

    NoteDto updateNote(UUID ownerId, String noteId, PatchNoteRequest patchNoteRequest);

    void deleteNote(UUID ownerId, String noteId);
}
