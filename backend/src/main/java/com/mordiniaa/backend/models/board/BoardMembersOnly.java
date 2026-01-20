package com.mordiniaa.backend.models.board;

import java.util.List;

public interface BoardMembersOnly{
    BoardMember getOwner();

    List<BoardMember> getMembers();
}
