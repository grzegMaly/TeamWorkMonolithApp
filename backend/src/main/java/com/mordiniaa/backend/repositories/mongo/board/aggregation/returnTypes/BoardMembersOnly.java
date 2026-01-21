package com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes;

import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.BoardMembers;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardMembersOnly implements BoardMembers {

    private BoardMember owner;
    private List<BoardMember> members;
}
