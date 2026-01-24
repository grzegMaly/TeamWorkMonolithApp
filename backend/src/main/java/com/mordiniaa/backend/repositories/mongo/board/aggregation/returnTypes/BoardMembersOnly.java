package com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes;

import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.board.BoardMembers;
import com.mordiniaa.backend.models.board.BoardTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardMembersOnly implements BoardMembers, BoardTemplate {

    private ObjectId id;
    private BoardMember owner;
    private List<BoardMember> members;
}
