package com.mordiniaa.backend.models.file.imageStorage;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@Document("image_metadata")
@TypeAlias("image_metadata")
public class ImageMetadata {

    @Id
    private ObjectId id;
    private String originalName;
    private String storedName;

    @Indexed(unique = true, name = "idx_userId")
    private UUID ownerId;
    private String mimeType;
    private long size;

    @CreatedDate
    private Instant createdAt;
}