package com.mordiniaa.backend.request.note;

import com.mordiniaa.backend.models.notes.regular.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRegularNoteRequest extends CreateNoteRequest {

    private Category category;
}
