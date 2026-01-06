package com.mordiniaa.backend.services;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.payload.ApiResponse;
import com.mordiniaa.backend.payload.CollectionResponse;

import java.util.UUID;

public interface NotesService {
    ApiResponse<NoteDto> getNoteById(String noteId);

    CollectionResponse<NoteDto> fetchAllNotesForUser(UUID ownerId);

    ApiResponse<NoteDto> createNote(NoteDto noteDto);

    ApiResponse<NoteDto> updateNote(NoteDto noteDto);

    void deleteNote(String noteId);
}
