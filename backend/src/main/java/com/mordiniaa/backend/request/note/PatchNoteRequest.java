package com.mordiniaa.backend.request.note;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchNoteRequest implements NoteRequest {

    private String title;
    private String content;
}
