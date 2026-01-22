package com.mordiniaa.backend.services.notes.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.request.task.PatchTaskDataRequest;
import com.mordiniaa.backend.services.notes.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskManagementService {

    private final UserRepresentationRepository userRepresentationRepository;
    private final MongoUserService mongoUserService;
    private final MongoIdUtils mongoIdUtils;
    private final BoardAggregationRepositoryImpl boardAggregationRepositoryImpl;
    private final BoardUtils boardUtils;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskDetailsDTO updateTask(UUID userId, String bId, String tId, PatchTaskDataRequest patchRequest) {

        mongoUserService.checkUserAvailability(userId);

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        ObjectId taskId = mongoIdUtils.getObjectId(tId);

        BoardMembersOnly board = boardAggregationRepositoryImpl
                .findBoardMembersForTask(boardId, userId, taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        BoardMember currentMember = boardUtils.getBoardMember(board, userId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        UUID boardOwner = board.getOwner().getUserId();
        UUID taskAuthor = task.getCreatedBy();

        if (!userId.equals(boardOwner) && !userId.equals(taskAuthor)) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        if (!task.getCreatedBy().equals(userId) && !currentMember.canUpdateTask()) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        if (patchRequest.getTitle() != null && !patchRequest.getTitle().isBlank())
            task.setTitle(patchRequest.getTitle());

        if (patchRequest.getDescription() != null && !patchRequest.getDescription().isBlank())
            task.setDescription(patchRequest.getDescription());

        if (patchRequest.getDescription() != null && patchRequest.getDeadline().isAfter(Instant.now()))
            task.setDeadline(patchRequest.getDeadline());

        Task savedTask = taskRepository.save(task);

        Set<UUID> usersIds = savedTask.getActivityElements()
                .stream().map(TaskActivityElement::getUser).collect(Collectors.toSet());
        Map<UUID, UserRepresentation> users = userRepresentationRepository.findAllByUserIdIn(usersIds)
                .stream()
                .collect(Collectors.toMap(
                        UserRepresentation::getUserId,
                        Function.identity()
                ));

        return taskMapper.toDetailedDto(savedTask, users);
    }

    public void assignUserToTask() {

    }

    public void removeUserFromTask() {

    }
}
