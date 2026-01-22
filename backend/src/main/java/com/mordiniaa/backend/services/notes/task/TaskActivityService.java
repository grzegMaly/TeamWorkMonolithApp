package com.mordiniaa.backend.services.notes.task;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.models.task.activity.TaskCategoryChange;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardWithTaskCategories;
import com.mordiniaa.backend.request.task.UpdateTaskPositionRequest;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskActivityService {

    private final MongoUserService mongoUserService;
    private final BoardRepository boardRepository;
    private final BoardAggregationRepository boardAggregationRepository;
    private final UserRepresentationRepository userRepresentationRepository;
    private final MongoIdUtils mongoIdUtils;
    private final BoardUtils boardUtils;
    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskShortDto changeTaskPosition(UUID userId, String bId, String tId, UpdateTaskPositionRequest request) {

        mongoUserService.checkUserAvailability(userId);

        ObjectId boardId = mongoIdUtils.getObjectId(bId);
        ObjectId taskId = mongoIdUtils.getObjectId(tId);

        BoardWithTaskCategories board = boardAggregationRepository.findBoardForTaskWithCategories(boardId, userId, taskId)
                        .orElseThrow(RuntimeException::new); // TODO: Change in Exceptions Section

        BoardMember currentMember = boardUtils.getBoardMember(board, userId);
        if (!currentMember.canMoveTaskBetweenCategories()) {
            throw new RuntimeException();
        }

        TaskCategory taskCategory = board.getTaskCategories()
                .stream()
                .filter(tC -> tC.getTasks().contains(taskId))
                .findFirst().orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        Task task = taskRepository.findById(taskId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        UUID boardOwner = board.getOwner().getUserId();
        if (task.getCreatedBy().equals(boardOwner) && !userId.equals(boardOwner)) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

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

    public void writeComment() {

    }

    public void updateComment() {

    }

    public void deleteOwnComment() {

    }

    public void deleteAnyComment() {

    }
}
