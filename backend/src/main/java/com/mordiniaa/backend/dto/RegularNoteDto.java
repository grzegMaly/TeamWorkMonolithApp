package com.mordiniaa.backend.dto;

import com.mordiniaa.backend.models.notes.regular.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class RegularNoteDto extends NoteDto {

    private Category category;
}
