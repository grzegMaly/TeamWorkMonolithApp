package com.mordiniaa.backend.request.note.deadline;

import com.mordiniaa.backend.models.notes.regular.Category;
import com.mordiniaa.backend.request.note.PatchNoteRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchRegularNoteRequest extends PatchNoteRequest {

    private Category category;
}
