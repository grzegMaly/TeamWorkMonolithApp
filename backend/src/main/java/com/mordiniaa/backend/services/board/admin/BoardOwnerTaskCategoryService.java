package com.mordiniaa.backend.services.board.admin;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import com.mordiniaa.backend.request.board.TaskCategoryRequest;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BoardOwnerTaskCategoryService {

    private final MongoUserService mongoUserService;
    private final BoardAggregationRepository boardAggregationRepository;
    private final MongoIdUtils mongoIdUtils;
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final MongoTemplate mongoTemplate;

    public BoardDetailsDto createTaskCategory(UUID boardOwner, String bId, TaskCategoryRequest request) {

        mongoUserService.checkUserAvailability(boardOwner);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        Board board = boardAggregationRepository.findFullBoardByIdAndOwner(boardId, boardOwner)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        String categoryName = request.getNewCategoryName().trim();
        int newPosition = board.getHighestTaskCategoryPosition() + 1;

        TaskCategory newCategory = new TaskCategory();
        newCategory.setPosition(newPosition);
        newCategory.setCategoryName(categoryName);

        Query query = Query.query(
                Criteria.where("_id").is(boardId)
                        .and("owner.userId").is(boardOwner)
                        .and("taskCategories.categoryName").ne(categoryName)
        );

        Update update = new Update()
                .push("taskCategories", newCategory)
                .inc("highestTaskCategoryPosition", 1);

        UpdateResult result = mongoTemplate.updateFirst(query, update, Board.class);
        if (result.getModifiedCount() == 0)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        BoardFull updatedBoard = boardAggregationRepository
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(boardOwner, boardId, board.getTeamId())
                .orElseThrow(RuntimeException::new);
        return boardMapper.toDetailedDto(updatedBoard);
    }

    public BoardDetailsDto renameTaskCategory(UUID boardOwner, String bId, UUID teamId, TaskCategoryRequest request) {

        mongoUserService.checkUserAvailability(boardOwner);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        String newCategoryName = request.getNewCategoryName().trim();

        String oldCatName = request.getExistingCategoryName();
        if (oldCatName == null || oldCatName.isBlank())
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        oldCatName = oldCatName.trim();

        Query query = Query.query(
                Criteria.where("_id").is(boardId)
                        .and("teamId").is(teamId)
                        .and("owner.userId").is(boardOwner)
                        .and("taskCategories.categoryName").is(oldCatName)
                        .and("taskCategories.categoryName").ne(newCategoryName)
        );

        Update update = new Update()
                .set("taskCategories.$[cat].categoryName", newCategoryName)
                .filterArray(Criteria.where("cat.categoryName").is(oldCatName));

        UpdateResult result = mongoTemplate.updateFirst(query, update, Board.class);
        if (result.getModifiedCount() == 0)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        BoardFull updatedBoard = boardAggregationRepository
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(boardOwner, boardId, teamId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        return boardMapper.toDetailedDto(updatedBoard);
    }

    public BoardDetailsDto reorderTaskCategories(UUID boardOwner, String bId, UUID teamId, TaskCategoryRequest request, int newPosition) {

        mongoUserService.checkUserAvailability(boardOwner);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        String catName = request.getExistingCategoryName();
        if (catName == null || catName.isBlank())
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        catName = catName.trim();

        Board board = boardAggregationRepository.findFullBoardByIdAndOwner(boardId, boardOwner)
                .orElseThrow(RuntimeException::new);

        if (newPosition > board.getHighestTaskCategoryPosition() || newPosition < 0)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        String categoryName = catName;
        TaskCategory category = board.getTaskCategories()
                .stream()
                .filter(tC -> tC.getCategoryName().equals(categoryName))
                .findFirst().orElse(null);

        if (category == null)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        int currentPosition = category.getPosition();
        if (currentPosition == newPosition)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Stream<TaskCategory> categoryStream = board.getTaskCategories().stream();
        if (newPosition > currentPosition)
            categoryStream.filter(tc -> tc.getPosition() > currentPosition && tc.getPosition() <= newPosition)
                    .forEach(TaskCategory::lowerPosition);
        else
            categoryStream.filter(tc -> tc.getPosition() < currentPosition && tc.getPosition() >= newPosition)
                    .forEach(TaskCategory::higherPosition);
        category.setPosition(newPosition);

        boardRepository.save(board);
        BoardFull updatedBoard = boardAggregationRepository
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(boardOwner, boardId, teamId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        return boardMapper.toDetailedDto(updatedBoard);
    }

    public BoardDetailsDto deleteTaskCategory(UUID boardOwner, String bId, UUID teamId, TaskCategoryRequest request) {

        mongoUserService.checkUserAvailability(boardOwner);
        ObjectId boardId = mongoIdUtils.getObjectId(bId);

        String catName = request.getExistingCategoryName();
        if (catName == null || catName.isBlank())
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        catName = catName.trim();

        Board board = boardRepository.findBoardByIdAndOwner_UserIdAndTeamId(boardId, boardOwner, teamId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        String categoryName = catName;
        TaskCategory taskCategory = board.getTaskCategories().stream()
                .filter(tC -> tC.getCategoryName().equals(categoryName))
                .findFirst()
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        int currentPosition = taskCategory.getPosition();
        board.getTaskCategories().stream().filter(tc -> tc.getPosition() > currentPosition)
                .forEach(TaskCategory::lowerPosition);

        Set<ObjectId> tasksIds = taskCategory.getTasks();
        board.removeTaskCategory(taskCategory);

        Query tasksQuery = Query.query(
                Criteria.where("_id").in(tasksIds)
        );

        DeleteResult result = mongoTemplate.remove(tasksQuery, Task.class);
        if (result.getDeletedCount() != tasksIds.size())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        boardRepository.save(board);
        BoardFull updatedBoard = boardAggregationRepository
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(boardOwner, boardId, teamId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        return boardMapper.toDetailedDto(updatedBoard);
    }
}
