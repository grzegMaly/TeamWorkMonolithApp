package com.mordiniaa.backend.services.notes.task;

import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.request.task.UpdateTaskPositionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskActivityService {

    private UserRepresentationRepository userRepresentationRepository;

    public void changeTaskCategoryAndPosition(UUID userId, String taskId, UpdateTaskPositionRequest request) {


    }

    public void changeTaskPosition() {

    }

    public void writeComment() {

    }

    public void updateComment() {

    }

    public void deleteOwnComment() {

    }

    public void deleteAnyComment() {

    }
}
