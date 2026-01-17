package com.mordiniaa.backend.dto.note;

import com.mordiniaa.backend.models.note.regular.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class RegularNoteDto extends NoteDto {

    private Category category;
}
