package com.mordiniaa.backend.request.note.regular;

import com.mordiniaa.backend.models.note.regular.Category;
import com.mordiniaa.backend.request.note.CreateNoteRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRegularNoteRequest extends CreateNoteRequest implements RegularNoteRequest {

    @NotNull(message = "Category is required")
    private Category category;
}
