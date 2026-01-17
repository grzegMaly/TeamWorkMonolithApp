package com.mordiniaa.backend.services.notes.task;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.UpdateResult;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
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

    public void getTaskDetailsById(UUID userId, String taskId) {

    }

    @Transactional
    public void createTask(UUID userId, String bId, String categoryName, CreateTaskRequest createTaskRequest) {

        if (!ObjectId.isValid(bId)) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }

        ObjectId boardId = new ObjectId(bId);

        boolean result = userRepresentationRepository.existsUserRepresentationByUserIdAndDeletedFalse(userId);
        if (!result) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }

        Board board = boardRepository.getBoardByIdWithCategoryAndBoardMember(boardId, categoryName, userId)
                .orElseThrow(RuntimeException::new); //TODO: Change in Exceptions Section

        BoardMember member = board.getMembers().stream().filter(bm -> bm.getUserId().equals(userId))
                .findAny().orElseThrow(RuntimeException::new);  //TODO: Change in Exceptions Section

        if (!member.getBoardPermissions().contains(BoardPermission.VIEW_BOARD) ||
                !member.getTaskPermissions().contains(TaskPermission.CREATE_TASK))
            throw new RuntimeException(); //TODO: Change in Exceptions Section

        Task task = new Task();
        if (createTaskRequest.getAssignedTo() != null) {
            if (!member.getTaskPermissions().contains(TaskPermission.ASSIGN_TASK))
                throw new RuntimeException(); //TODO: Change in Exceptions Section

            Set<UUID> membersIds = board.getMembers().stream().map(BoardMember::getUserId)
                    .collect(Collectors.toSet());
            if (!membersIds.containsAll(createTaskRequest.getAssignedTo())) {
                throw new RuntimeException(); //TODO: Change in Exceptions Section
            }
            task.setAssignedTo(createTaskRequest.getAssignedTo());
        }

        task.setCreatedBy(member.getUserId());
        task.setTitle(createTaskRequest.getTitle());
        task.setDescription(createTaskRequest.getDescription());
        task.setDeadline(createTaskRequest.getDeadline());
        task.setPositionInCategory(0);

        Set<ObjectId> taskIds = board.getTaskCategories().getFirst().getTasks();

        Query query = new Query(
                Criteria.where("_id").in(taskIds)
        );

        Update update = new Update()
                .inc("positionInCategory", 1);

        UpdateResult updatePositionResult = mongoTemplate.updateMulti(query, update, Task.class);

        Task savedTask = taskRepository.save(task);
        board.getTaskCategories().getFirst().addTaskId(savedTask.getId());
        boardRepository.save(board);

    }

    public void deleteTaskFromBoard(UUID userId, String taskId) {

    }
}
