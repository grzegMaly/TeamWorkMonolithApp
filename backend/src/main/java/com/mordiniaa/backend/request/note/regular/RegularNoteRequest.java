package com.mordiniaa.backend.request.note.regular;

import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.request.note.NoteRequest;

public interface RegularNoteRequest extends NoteRequest {

    Category getCategory();
}
