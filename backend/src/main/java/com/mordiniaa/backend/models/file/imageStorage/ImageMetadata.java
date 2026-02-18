package com.mordiniaa.backend.models.file.imageStorage;

import jakarta.persistence.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("image_metadata")
public record ImageMetadata(
        @Id ObjectId id,
        String originalName,
        String storedName,
        String mimeType,
        long size,
        Instant createdAt
) {}