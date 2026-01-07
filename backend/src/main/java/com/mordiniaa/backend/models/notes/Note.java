package com.mordiniaa.backend.models.notes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
public abstract class Note {

    @Id
    @Field(order = 1)
    private ObjectId id;

    @TextIndexed
    @Field(name = "title", order = 2)
    private String title;

    @Indexed
    @Field(name = "ownerId", order = 3)
    private UUID ownerId;

    @TextIndexed
    @Field(name = "content", order = 4)
    private String content;

    @Indexed
    @Field(name = "archived", order = 5)
    private boolean archived;

    @CreatedDate
    @Field(name = "createdAt", order = 200)
    private Instant createdAt;

    @LastModifiedDate
    @Field(name = "updatedAt", order = 201)
    private Instant updatedAt;
}
