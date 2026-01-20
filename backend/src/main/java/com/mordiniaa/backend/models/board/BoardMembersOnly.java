package com.mordiniaa.backend.models.board;

import java.util.List;

public record BoardMembersOnly(
        BoardMember owner,
        List<BoardMember> members
) {
}
