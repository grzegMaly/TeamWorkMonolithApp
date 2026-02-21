package com.mordiniaa.backend.controllers.secured.manager;

import com.mordiniaa.backend.request.board.TaskCategoryRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manager/board/{boardId}/category")
public class BoardTaskCategoryManagerController {

    @PostMapping
    public void createTaskCategory(
            @PathVariable String boardId,
            @RequestBody TaskCategoryRequest taskCategoryRequest
    ) {

    }

    @PutMapping("/rename")
    public void renameTaskCategory(
        @PathVariable String boardId,
        @RequestParam(name = "t") UUID teamId,
        @RequestBody TaskCategoryRequest taskCategoryRequest
    ) {

    }

    @PutMapping("/reorder")
    public void reorderTaskCategories(
            @PathVariable String boardId,
            @RequestParam(name = "t") UUID teamId,
            @RequestParam(name = "p") Integer newPosition,
            @RequestBody TaskCategoryRequest taskCategoryRequest
    ) {

    }

    @DeleteMapping
    public void deleteTaskCategory(
            @PathVariable String boardId,
            @RequestParam(name = "t") UUID teamId,
            @RequestBody TaskCategoryRequest taskCategoryRequest
    ) {

    }
}
