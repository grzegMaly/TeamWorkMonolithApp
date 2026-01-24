package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.repositories.mongo.user.aggregation.UserReprCustomRepository;
import com.mordiniaa.backend.request.task.AssignUsersRequest;
import com.mordiniaa.backend.request.task.PatchTaskDataRequest;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskManagementService {

    private final MongoIdUtils mongoIdUtils;
    private final BoardAggregationRepository boardAggregationRepository;
    private final BoardUtils boardUtils;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final UserReprCustomRepository userReprCustomRepository;

    public TaskDetailsDTO updateTask(UUID userId, String bId, String tId, PatchTaskDataRequest patchRequest) {

        BiFunction<ObjectId, ObjectId, BoardMembersOnly> boardFunction = (boardId, taskId) -> {
            return boardAggregationRepository
                    .findBoardMembersForTask(boardId, userId, taskId)
                    .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
        };

        BiFunction<BoardMembersOnly, ObjectId, TaskDetailsDTO> taskFunction = (board, taskId) -> {
            BoardMember currentMember = boardUtils.getBoardMember(board, userId);
            Task task = taskService.findTaskById(taskId);

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
            return taskService.detailedTaskDto(savedTask, usersIds);
        };

        return taskService.executeTaskOperation(userId, bId, tId, boardFunction, taskFunction);
    }

    public TaskDetailsDTO assignUsersToTask(UUID assigningId, AssignUsersRequest assignRequest, String bId, String tId) {

        Set<UUID> toAssign = assignRequest.getUsers();

        Set<UUID> usersToCheck = new HashSet<>(toAssign);
        usersToCheck.add(assigningId);

        boolean result = userReprCustomRepository.allUsersAvailable(usersToCheck);
        if (!result)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        ObjectId taskId = mongoIdUtils.getObjectId(tId);

        BoardMembersOnly board = boardAggregationRepository
                .findBoardMembersForTask(boardId, assigningId, taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        BoardMember currentUser = boardUtils.getBoardMember(board, assigningId);
        if (!currentUser.canAssignTask())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Set<UUID> membersIds = board.getMembers().stream()
                .map(BoardMember::getUserId)
                .collect(Collectors.toSet());
        membersIds.add(assigningId);

        if (!membersIds.containsAll(toAssign))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        UUID boardOwner = board.getOwner().getUserId();
        if (!boardOwner.equals(assigningId) && toAssign.contains(boardOwner))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Task task = taskService.findTaskById(taskId);

        if (task.getAssignedTo().containsAll(toAssign))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        task.addMembers(toAssign);

        Task savedTask = taskRepository.save(task);
        Set<UUID> usersIds = savedTask.getActivityElements()
                .stream().map(TaskActivityElement::getUser).collect(Collectors.toSet());

        return taskService.detailedTaskDto(task, usersIds);
    }

    public void removeUserFromTask(UUID userId, UUID toDeleteId, String bId, String tId) {

        BiFunction<ObjectId, ObjectId, BoardMembersOnly> boardFunction = (boardId, taskId) -> {
            return boardAggregationRepository
                    .findBoardMembersForTask(boardId, userId, taskId)
                    .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
        };

        BiFunction<BoardMembersOnly, ObjectId, TaskShortDto> taskFunction =  (board, taskId) -> {
            BoardMember currentMember = boardUtils.getBoardMember(board, userId);
            if (!currentMember.canUnassignTask())
                throw new RuntimeException(); // TODO: Change In Exceptions Section

            UUID boardOwner = board.getOwner().getUserId();
            if (toDeleteId.equals(boardOwner)) {
                if (!userId.equals(boardOwner))
                    throw new RuntimeException(); // TODO: Change In Exceptions Section
            }

            Set<UUID> membersIds = board.getMembers().stream()
                    .map(BoardMember::getUserId)
                    .collect(Collectors.toSet());
            membersIds.add(boardOwner);

            Task task = taskService.findTaskById(taskId);
            if (!task.getAssignedTo().contains(toDeleteId))
                throw new RuntimeException(); // TODO: Change In Exceptions Section

            task.removeMember(toDeleteId);

            taskRepository.save(task);
            return null;
        };
        taskService.executeTaskOperation(userId, bId, tId, boardFunction, taskFunction);
    }
}
