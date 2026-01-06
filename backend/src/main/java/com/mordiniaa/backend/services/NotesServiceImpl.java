package com.mordiniaa.backend.services;

import com.mordiniaa.backend.repositories.NotesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {

    private NotesRepository notesRepository;
}
