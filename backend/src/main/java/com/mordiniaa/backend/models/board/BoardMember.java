package com.mordiniaa.backend.models.board;

import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BoardMember {

    private UUID userId;
    private Set<BoardPermission> boardPermissions = new HashSet<>();
    private Set<CategoryPermissions> categoryPermissions = new HashSet<>();
    private Set<TaskPermission> taskPermissions = new HashSet<>();
    private Set<CommentPermission> commentPermissions = new HashSet<>(Set.of(
            CommentPermission.COMMENT_TASK,
            CommentPermission.DELETE_OWN_COMMENT,
            CommentPermission.EDIT_OWN_COMMENT
    ));

    public BoardMember(UUID userId) {
        this.userId = userId;
    }

    public boolean hasFullPermissions() {
        return boardPermissions.containsAll(List.of(BoardPermission.values()))
                && categoryPermissions.containsAll(List.of(CategoryPermissions.values()))
                && taskPermissions.containsAll(List.of(TaskPermission.values()))
                && commentPermissions.containsAll(List.of(CommentPermission.values()));
    }

    public boolean canViewBoard() {
        return boardPermissions.contains(BoardPermission.VIEW_BOARD);
    }

    public boolean canCreateTask() {
        return canViewBoard()
                && taskPermissions.contains(TaskPermission.CREATE_TASK);
    }

    public boolean canAssignTask() {
        return taskPermissions.contains(TaskPermission.ASSIGN_TASK);
    }

    public boolean canDeleteTask() {
        return taskPermissions.contains(TaskPermission.DELETE_TASK);
    }

    public boolean canMoveTaskBetweenCategories() {
        return canViewBoard()
                && categoryPermissions.contains(CategoryPermissions.MOVE_TASK_BETWEEN_CATEGORIES);
    }

    public boolean canCommentTask() {
        return canViewBoard()
                && commentPermissions.contains(CommentPermission.COMMENT_TASK);
    }

    public boolean canUpdateOwnComment() {
        return canViewBoard()
                && commentPermissions.contains(CommentPermission.EDIT_OWN_COMMENT);
    }

    public boolean canDeleteOwnComment() {
        return canViewBoard()
                && commentPermissions.contains(CommentPermission.DELETE_OWN_COMMENT);
    }

    public boolean canDeleteAnyComment() {
        return canViewBoard()
                && commentPermissions.contains(CommentPermission.DELETE_ANY_COMMENT);
    }
}
