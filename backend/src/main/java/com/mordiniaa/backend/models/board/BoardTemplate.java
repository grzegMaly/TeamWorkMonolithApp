package com.mordiniaa.backend.models.board;

import org.bson.types.ObjectId;

import java.time.Instant;

public interface BoardTemplate {

    ObjectId getId();
    String getBoardName();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}
