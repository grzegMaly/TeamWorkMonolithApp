package com.mordiniaa.backend.controllers.notesController;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.payload.CollectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notes/archive")
public class ArchivedNotesController {

    @GetMapping
    public ResponseEntity<CollectionResponse<NoteDto>> getAllArchivedNotesForUser() {
        return null;
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Void> switchArchived() {
        return null;
    }
}
