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
import java.util.UUID;

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
}

