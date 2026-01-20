package com.mordiniaa.backend.services.notes.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.BoardMembersOnly;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.request.task.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.validator.cfg.defs.UUIDDef;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserRepresentationRepository userRepresentationRepository;
    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;
    private final TaskMapper taskMapper;

    public TaskDetailsDTO getTaskDetailsById(UUID userId, String bId, String tId) {

        checkUserAvailability(userId);

        ObjectId boardId = getObjectId(bId);
        ObjectId taskId = getObjectId(tId);
        BoardMembersOnly board = boardRepository.getBoardWithMembersByBoardIdAndMemberIdOrOwnerIdAndTaskId(boardId, userId, taskId)
                .orElseThrow(RuntimeException::new); //TODO: Change in Exceptions Section

        Set<BoardMember> allMembers = new HashSet<>(board.getMembers());
        allMembers.add(board.getOwner());

        BoardMember currentMember = allMembers.stream().filter(mb -> mb.getUserId().equals(userId))
                .findFirst().orElseThrow(RuntimeException::new); //TODO: Change in Exceptions Section

        if (!currentMember.canViewBoard()) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(RuntimeException::new); //TODO: Change in Exceptions Section

        Set<UUID> userIds = allMembers.stream().map(BoardMember::getUserId)
                .collect(Collectors.toSet());
        Map<UUID, UserRepresentation> users =userRepresentationRepository.findAllByUserIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(
                        UserRepresentation::getUserId,
                        Function.identity()
                ));

        return taskMapper.toDetailedDto(task, users);
    }

    @Transactional
    public TaskShortDto createTask(UUID userId, String bId, String categoryName, CreateTaskRequest createTaskRequest) {

        checkUserAvailability(userId);

        ObjectId boardId = new ObjectId(bId);
        Board board = boardRepository.getBoardByIdWithCategoryAndBoardMemberOrOwner(boardId, categoryName, userId)
                .orElseThrow(RuntimeException::new); //TODO: Change in Exceptions Section

        BoardMember currentMember = getBoardMember(board, userId);
        if (!board.getOwner().getUserId().equals(userId)
                && createTaskRequest.getAssignedTo().contains(board.getOwner().getUserId())) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }

        if (!currentMember.canCreateTask())
            throw new RuntimeException(); //TODO: Change in Exceptions Section

        Task task = new Task();
        if (createTaskRequest.getAssignedTo() != null) {

            Set<UUID> assignedTo = new HashSet<>(createTaskRequest.getAssignedTo());

            if (assignedTo.contains(currentMember.getUserId())) {
                task.addMember(currentMember.getUserId());
                assignedTo.remove(currentMember.getUserId());
            }

            if (!assignedTo.isEmpty()) {
                if (!currentMember.canAssignTask())
                    throw new RuntimeException(); //TODO: Change in Exceptions Section

                Set<UUID> membersIds = board.getMembers().stream().map(BoardMember::getUserId)
                        .collect(Collectors.toSet());
                if (!membersIds.containsAll(assignedTo)) {
                    throw new RuntimeException(); //TODO: Change in Exceptions Section
                }
                task.addMembers(assignedTo);
            }
        }

        task.setCreatedBy(currentMember.getUserId());
        task.setTitle(createTaskRequest.getTitle());
        task.setDescription(createTaskRequest.getDescription());
        task.setDeadline(createTaskRequest.getDeadline());
        task.setPositionInCategory(0);

        Set<ObjectId> taskIds = board.getTaskCategories().getFirst().getTasks();
        if (!taskIds.isEmpty()) {
            Query query = new Query(
                    Criteria.where("_id").in(taskIds)
            );

            Update update = new Update()
                    .inc("positionInCategory", 1);

            mongoTemplate.updateMulti(query, update, Task.class);
        }

        Task savedTask = taskRepository.save(task);
        board.getTaskCategories().getFirst().addTaskId(savedTask.getId());
        mongoTemplate.updateFirst(Query.query(
                        Criteria.where("_id").is(board.getId())
                                .and("taskCategories.categoryName").is(categoryName)
                ),
                new Update().push("taskCategories.$.tasks", savedTask.getId()),
                Board.class);
        return taskMapper.toShortenedDto(savedTask);
    }

    public void deleteTaskFromBoard(UUID userId, String taskId) {

    }

    private ObjectId getObjectId(String id) {
        if (!ObjectId.isValid(id)) {
            throw new RuntimeException(); // TODO: Change in Exceptions Section
        }
        return new ObjectId(id);
    }

    private void checkUserAvailability(UUID userId) {
        boolean result = userRepresentationRepository.existsUserRepresentationByUserIdAndDeletedFalse(userId);
        if (!result) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }
    }

    private BoardMember getBoardMember(Board board, UUID userId) {
        if (board.getOwner().getUserId().equals(userId)) {
            return board.getOwner();
        } else {
            return board.getMembers().stream().filter(bm -> bm.getUserId().equals(userId))
                    .findFirst().orElseThrow(RuntimeException::new);
        }
    }
}
