package com.mordiniaa.backend.services;

import com.mordiniaa.backend.dto.NoteDto;
import com.mordiniaa.backend.mappers.notes.NoteMapper;
import com.mordiniaa.backend.payload.ApiResponse;
import com.mordiniaa.backend.payload.CollectionResponse;
import com.mordiniaa.backend.repositories.mongo.NotesRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final NoteMapper noteMapper;

    @Override
    public ApiResponse<NoteDto> getNoteById(String noteId) {
        ObjectId id = new ObjectId(noteId);
        return null;
    }

    @Override
    public CollectionResponse<NoteDto> fetchAllNotesForUser(UUID ownerId) {

        return null;
    }

    @Override
    public ApiResponse<NoteDto> createNote(NoteDto noteDto) {
        return null;
    }

    @Override
    public ApiResponse<NoteDto> updateNote(NoteDto noteDto) {
        return null;
    }

    @Override
    public void deleteNote(String noteId) {
        ObjectId id = new ObjectId(noteId);
    }
}
