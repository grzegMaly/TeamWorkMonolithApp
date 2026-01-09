package com.mordiniaa.backend.request.note;

import com.mordiniaa.backend.models.notes.regular.Category;

public interface RegularNoteRequest extends NoteRequest {

    Category getCategory();
}
