package com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes;


import java.util.UUID;

public interface TaskCreatorProjection {

    UUID getCreatedBy();
}
