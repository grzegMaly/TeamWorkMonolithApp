package com.mordiniaa.backend.request.board;

import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PermissionsRequest {

    private Set<BoardPermission> boardPermissions;
    private Set<CategoryPermissions> categoryPermissions;
    private Set<TaskPermission> taskPermissions;
    private Set<CommentPermission> commentPermissions;
}
