package com.mordiniaa.backend.models.user.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document("users")
public class UserRepresentation {

    @Id
    private ObjectId id;

    @Indexed
    @Field("userId")
    private UUID userId;

    @Field("username")
    private String username;

    @Field("imageUrl")
    private String imageUrl;

    @Field("deleted")
    private boolean deleted = false;
}
