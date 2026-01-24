package com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TaskCreatorProjectionWithOptPosition {

    private ObjectId id;
    private UUID createdBy;

    private Integer taskPosition;
}
