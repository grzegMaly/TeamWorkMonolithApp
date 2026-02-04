package com.mordiniaa.backend.models.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document("boards")
@TypeAlias("board")
public class Board implements BoardMembers, BoardTemplate {

    @Id
    private ObjectId id;

    @Indexed
    @Field("owner")
    private BoardMember owner;

    @Indexed
    @Field("teamId")
    private UUID teamId;

    @Indexed
    @Field("boardName")
    private String boardName;

    @Field("taskCategories")
    private List<TaskCategory> taskCategories;

    @Field("members")
    private List<BoardMember> members;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;

    @Field("archived")
    private boolean archived = false;

    @Field("deleted")
    private boolean deleted = false;

    public void addMember(BoardMember member) {
        if (!members.contains(member))
            members.add(member);
    }

    public void removeMember(UUID userId) {
        members.stream()
                .filter(bm -> bm.getUserId().equals(userId))
                .findFirst()
                .ifPresent(member -> members.remove(member));
    }
}

