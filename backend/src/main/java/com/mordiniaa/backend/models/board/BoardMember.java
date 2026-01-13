package com.mordiniaa.backend.models.board;

import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import com.mordiniaa.backend.models.board.permissions.WorkspacePermission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BoardMember {

    private UUID userId;
    private Set<WorkspacePermission> workspacePermissions = new HashSet<>();
    private Set<CategoryPermissions> categoryPermissions = new HashSet<>();
    private Set<TaskPermission> taskPermissions = new HashSet<>();
    private Set<CommentPermission> commentPermissions = new HashSet<>(Set.of(
            CommentPermission.COMMENT_TASK,
            CommentPermission.DELETE_OWN_COMMENT,
            CommentPermission.EDIT_OWN_COMMENT
    ));
}
