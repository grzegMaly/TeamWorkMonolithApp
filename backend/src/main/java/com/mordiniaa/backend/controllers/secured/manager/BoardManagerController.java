package com.mordiniaa.backend.controllers.secured.manager;

import com.mordiniaa.backend.request.board.BoardCreationRequest;
import com.mordiniaa.backend.request.board.PermissionsRequest;
import com.mordiniaa.backend.services.board.owner.BoardOwnerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manager/board")
public class BoardManagerController {

    private final BoardOwnerService boardOwnerService;

    public BoardManagerController(BoardOwnerService boardOwnerService) {
        this.boardOwnerService = boardOwnerService;
    }

    public void changeBoardMemberPermissions(@Valid @RequestBody PermissionsRequest permissionsRequest) {

    }

    @PutMapping("/archive")
    public void archiveBoard(
            @RequestParam(name = "u") UUID ownerId,
            @RequestParam(name = "b") String boardId
    ) {

    }

    @PutMapping("/restore")
    public void restoreBoard(
            @RequestParam(name = "u") UUID ownerId,
            @RequestParam(name = "b") String boardId
    ) {

    }

    @PostMapping
    public void createBoard(@Valid @RequestBody BoardCreationRequest boardCreationRequest) {

    }

    @PutMapping("/user/{operation}")
    public void addUserToBoard(
            @PathVariable String operation,
            @RequestParam(name = "u") UUID userId,
            @RequestParam(name = "b") String boardId
    ) {
        if (operation.equals("add")) {
            boardOwnerService.addUserToBoard(UUID.randomUUID(), userId, boardId);
        } else if (operation.equals("remove")) {
            boardOwnerService.removeUserFromBoard(UUID.randomUUID(), userId, boardId);
        }
    }

    public void deleteBoard(@RequestParam(name = "b") String boardId) {

    }


}
