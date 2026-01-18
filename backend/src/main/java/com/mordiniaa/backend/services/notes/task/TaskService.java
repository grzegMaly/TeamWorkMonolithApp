package com.mordiniaa.backend.services.notes.task;

import com.mordiniaa.backend.dto.task.TaskCardDto;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.task.Task;
import com.mordiniaa.backend.repositories.mongo.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.request.board.task.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserRepresentationRepository userRepresentationRepository;
    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;
    private final TaskMapper taskMapper;

    public void getTaskDetailsById(UUID userId, String taskId) {

    }

    @Transactional
    public TaskCardDto createTask(UUID userId, String bId, String categoryName, CreateTaskRequest createTaskRequest) {

        if (!ObjectId.isValid(bId)) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }

        ObjectId boardId = new ObjectId(bId);

        boolean result = userRepresentationRepository.existsUserRepresentationByUserIdAndDeletedFalse(userId);
        if (!result) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }

        Board board = boardRepository.getBoardByIdWithCategoryAndBoardMemberOrOwner(boardId, categoryName, userId)
                .orElseThrow(RuntimeException::new); //TODO: Change in Exceptions Section

        BoardMember currentMember;
        if (board.getOwner().getUserId().equals(userId)) {
            currentMember = board.getOwner();
        } else {
            currentMember = board.getMembers().stream().filter(bm -> bm.getUserId().equals(userId))
                    .findFirst().orElseThrow(RuntimeException::new);
            if (createTaskRequest.getAssignedTo().contains(board.getOwner().getUserId())) {
                throw new RuntimeException(); //TODO: Change in Exceptions Section
            }
        }

        if (!currentMember.canCreateTask())
            throw new RuntimeException(); //TODO: Change in Exceptions Section

        Task task = new Task();
        if (createTaskRequest.getAssignedTo() != null) {

            Set<UUID> assignedTo = createTaskRequest.getAssignedTo();

            if (assignedTo.contains(currentMember.getUserId()))
                task.addMember(currentMember.getUserId());

            if (assignedTo.size() > 1) {
                if (!currentMember.canAssignTask())
                    throw new RuntimeException(); //TODO: Change in Exceptions Section

                Set<UUID> membersIds = board.getMembers().stream().map(BoardMember::getUserId)
                        .collect(Collectors.toSet());
                if (!membersIds.containsAll(assignedTo)) {
                    throw new RuntimeException(); //TODO: Change in Exceptions Section
                }
                task.addMembers(createTaskRequest.getAssignedTo());
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
        boardRepository.save(board);
        return taskMapper.toShortenedDto(savedTask);
    }

    public void deleteTaskFromBoard(UUID userId, String taskId) {

    }
}
