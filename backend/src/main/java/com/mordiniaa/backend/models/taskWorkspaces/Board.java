package com.mordiniaa.backend.models.taskWorkspaces;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document("boards")
public class Board {

    @Id
    private ObjectId id;

    @Field("owner")
    private BoardMember owner;

    @Field("boardName")
    private String boardName;

    @Field("taskCategories")
    private List<TaskCategory> taskCategories;

    @Field("members")
    private List<BoardMember> members;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;
}
