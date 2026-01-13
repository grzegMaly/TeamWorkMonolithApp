package com.mordiniaa.backend.services.notes.notes;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.utils.PageResult;

import java.util.List;
import java.util.UUID;

public interface ArchivedNotesService {
    PageResult<List<NoteDto>> fetchAllArchivedNotes(UUID ownerId, int pageNumber, int pageSize);

    void switchArchivedNoteForUser(UUID ownerId, String noteId);
}
