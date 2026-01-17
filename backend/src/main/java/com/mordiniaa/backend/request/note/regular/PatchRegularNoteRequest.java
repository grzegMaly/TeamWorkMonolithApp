package com.mordiniaa.backend.request.note.regular;

import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.request.note.PatchNoteRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchRegularNoteRequest extends PatchNoteRequest implements RegularNoteRequest {

    private Category category;
}
