package com.mordiniaa.backend.controllers.notesController;

import com.mordiniaa.backend.config.NotesConstants;
import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.payload.ApiResponse;
import com.mordiniaa.backend.payload.CollectionResponse;
import com.mordiniaa.backend.services.notes.NotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;

    @GetMapping
    public ResponseEntity<CollectionResponse<NoteDto>> fetchAllNotesForUser(
            @RequestParam(name = "pn", required = false, defaultValue = NotesConstants.PAGE_NUMBER) int pageNumber,
            @RequestParam(name = "ps", required = false, defaultValue = NotesConstants.PAGE_SIZE) int pageSize,
            @RequestParam(name = "pso", required = false, defaultValue = NotesConstants.SORT_ORDER) String sortOrder,
            @RequestParam(name = "psk", required = false, defaultValue = "") String sortKey,
            @RequestParam(name = "key", required = false, defaultValue = "") String keyword
    ) {
        return null;
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteDto>> getNoteById(@PathVariable String noteId) {
        return notesService.getNoteById(
                        noteId,
                        UUID.randomUUID() //TODO: Get id from user in security section
                )
                .map(dto -> ResponseEntity.ok(new ApiResponse<>("Success", dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoteDto>> createNote(@RequestBody NoteDto noteDto) {
        return null;
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteDto>> updateNote(@PathVariable String noteId, @RequestBody NoteDto noteDto) {
        return null;
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNodeById(@PathVariable String noteId) {
        return null;
    }
}
