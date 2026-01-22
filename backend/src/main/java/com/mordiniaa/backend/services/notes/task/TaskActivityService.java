package com.mordiniaa.backend.services.notes.task;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.activity.TaskActivityElement;
import com.mordiniaa.backend.models.task.activity.TaskCategoryChange;
import com.mordiniaa.backend.models.task.activity.TaskComment;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardWithTaskCategories;
import com.mordiniaa.backend.request.task.UpdateTaskPositionRequest;
import com.mordiniaa.backend.request.task.UploadCommentRequest;
import com.mordiniaa.backend.services.notes.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskActivityService {

    private final MongoUserService mongoUserService;
    private final BoardAggregationRepository boardAggregationRepository;
    private final MongoIdUtils mongoIdUtils;
    private final BoardUtils boardUtils;
    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;
    private final TaskMapper taskMapper;
    private final UserRepresentationRepository userRepresentationRepository;

    @Transactional
    public TaskShortDto changeTaskPosition(UUID userId, String bId, String tId, UpdateTaskPositionRequest request) {

        mongoUserService.checkUserAvailability(userId);

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        ObjectId taskId = mongoIdUtils.getObjectId(tId);

        BoardWithTaskCategories board = boardAggregationRepository.findBoardForTaskWithCategories(boardId, userId, taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change in Exceptions Section

        BoardMember currentMember = boardUtils.getBoardMember(board, userId);
        if (!currentMember.canMoveTaskBetweenCategories())
            throw new RuntimeException();

        TaskCategory taskCategory = board.getTaskCategories()
                .stream()
                .filter(tC -> tC.getTasks().contains(taskId))
                .findFirst().orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        Task task = findTaskById(taskId);

        UUID boardOwner = board.getOwner().getUserId();
        if (task.getCreatedBy().equals(boardOwner) && !userId.equals(boardOwner))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        if (request.getNewTaskCategory() != null) {
            String newCategory = request.getNewTaskCategory();
            TaskCategory newTaskCategory = board.getTaskCategories()
                    .stream()
                    .filter(tC -> tC.getCategoryName().equals(newCategory))
                    .findFirst()
                    .orElseThrow(RuntimeException::new); // TODO: Change In Category Section

            Update pollPushUpdate = new Update()
                    .pull("taskCategories.$[from].tasks", taskId)
                    .push("taskCategories.$[to].tasks", taskId);

            UpdateOptions options = new UpdateOptions().arrayFilters(List.of(
                    Filters.eq("from.categoryName", taskCategory.getCategoryName()),
                    Filters.eq("to.categoryName", newCategory)
            ));

            MongoCollection<Document> boardCollection = mongoTemplate.getCollection("boards");
            boardCollection.updateOne(
                    Filters.eq("_id", boardId),
                    pollPushUpdate.getUpdateObject(),
                    options
            );

            TaskCategoryChange taskCategoryChange = new TaskCategoryChange(userId);
            taskCategoryChange.setPrevCategory(taskCategory.getCategoryName());
            taskCategoryChange.setNextCategory(newCategory);
            task.addTaskActivityElement(taskCategoryChange);

            taskCategory = newTaskCategory;
        }

        Query incPositionsQuery = Query.query(
                Criteria.where("_id").in(taskCategory.getTasks())
                        .and("positionInCategory").gte(request.getNewPosition())
        );
        Update inPositionsUpdate = new Update()
                .inc("positionInCategory", 1);
        mongoTemplate.updateMulti(incPositionsQuery, inPositionsUpdate, Task.class);

        task.setPositionInCategory(request.getNewPosition());
        task = taskRepository.save(task);

        return taskMapper.toShortenedDto(task);
    }

    public TaskDetailsDTO writeComment(UUID userId, String bId, String tId, UploadCommentRequest uploadCommentRequest) {

        mongoUserService.checkUserAvailability(userId);

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        ObjectId taskId = mongoIdUtils.getObjectId(tId);

        BoardWithTaskCategories board = boardAggregationRepository.findBoardForTaskWithCategories(boardId, userId, taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        BoardMember currentMember = boardUtils.getBoardMember(board, userId);
        if (!currentMember.canCommentTask())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Task task = findTaskById(taskId);

        if (!task.getAssignedTo().contains(currentMember.getUserId()))
            if (!currentMember.getUserId().equals(board.getOwner().getUserId()))
                throw new RuntimeException(); // TODO: Change In Exceptions Section

        TaskComment taskComment = new TaskComment(userId);
        taskComment.setComment(uploadCommentRequest.getComment());
        task.addTaskActivityElement(taskComment);
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

    public TaskDetailsDTO updateComment(UUID userId, String bId, String tId, UploadCommentRequest uploadCommentRequest) {

        if (uploadCommentRequest.getCommentId() == null)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        mongoUserService.checkUserAvailability(userId);

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        ObjectId taskId = mongoIdUtils.getObjectId(tId);

        BoardWithTaskCategories board = boardAggregationRepository.findBoardForTaskWithCategories(boardId, userId, taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        BoardMember currentMember = boardUtils.getBoardMember(board, userId);
        if (!currentMember.canViewBoard())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Task task = findTaskById(taskId);
        TaskComment taskComment = getTaskComment(task, uploadCommentRequest.getCommentId());

        if (taskComment.isUpdated())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        if (!taskComment.getUser().equals(userId))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        if (!currentMember.canUpdateOwnComment())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        taskComment.setComment(uploadCommentRequest.getComment());
        taskComment.setUpdated(true);

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

    public TaskDetailsDTO deleteComment(UUID userId, String bId, String tId, UUID commentId) {

        mongoUserService.checkUserAvailability(userId);

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        ObjectId taskId = mongoIdUtils.getObjectId(tId);

        BoardWithTaskCategories board = boardAggregationRepository.findBoardForTaskWithCategories(boardId, userId, taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        BoardMember currentMember = boardUtils.getBoardMember(board, userId);
        if (!currentMember.canViewBoard())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Task task = findTaskById(taskId);
        TaskComment taskComment = getTaskComment(task, commentId);

        UUID boardOwner = board.getOwner().getUserId();
        if (taskComment.getUser().equals(boardOwner) && !userId.equals(boardOwner))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        if (!task.getAssignedTo().contains(userId)) {
            if (!userId.equals(board.getOwner().getUserId()) || !currentMember.canDeleteAnyComment()) {
                throw new RuntimeException(); // TODO: Change In Exceptions Section
            }
        } else {
            if (taskComment.getUser().equals(userId)) {
                if (!currentMember.canDeleteOwnComment())
                    throw new RuntimeException(); // TODO: Change In Exceptions Section
            } else {
                if (!currentMember.canDeleteAnyComment())
                    throw new RuntimeException(); // TODO: Change In Exceptions Section
            }
        }

        boolean result = task.getActivityElements()
                .removeIf(element -> element instanceof TaskComment tc && tc.getCommentId().equals(commentId));

        if (!result)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

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

    private Task findTaskById(ObjectId taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
    }

    private TaskComment getTaskComment(Task task, UUID commentId) {
        return task.getActivityElements()
                .stream()
                .filter(taskActivityElement -> taskActivityElement instanceof TaskComment)
                .map(element -> (TaskComment) element)
                .filter(tC -> tC.getCommentId().equals(commentId))
                .findFirst().orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
    }
}
