package com.mordiniaa.backend.models.board;

import java.util.List;

public interface BoardMembers {

    BoardMember getOwner();

    List<BoardMember> getMembers();
}
