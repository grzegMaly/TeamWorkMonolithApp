package com.mordiniaa.backend.request.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateNoteRequest implements NoteRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 40, message = "Title must be between 3 and 40 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 512, message = "Content max length is 512 characters")
    private String content;
}
