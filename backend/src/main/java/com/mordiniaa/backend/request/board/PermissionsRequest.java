package com.mordiniaa.backend.request.board;

import com.mordiniaa.backend.models.board.permissions.BoardPermission;
import com.mordiniaa.backend.models.board.permissions.CategoryPermissions;
import com.mordiniaa.backend.models.board.permissions.CommentPermission;
import com.mordiniaa.backend.models.board.permissions.TaskPermission;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PermissionsRequest {

    @NotNull
    private Set<BoardPermission> boardPermissions;

    @NotNull
    private Set<CategoryPermissions> categoryPermissions;

    @NotNull
    private Set<TaskPermission> taskPermissions;

    @NotNull
    private Set<CommentPermission> commentPermissions;
}
