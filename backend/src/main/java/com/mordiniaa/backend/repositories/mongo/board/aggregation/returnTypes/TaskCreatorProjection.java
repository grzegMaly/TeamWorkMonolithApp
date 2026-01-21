package com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TaskCreatorProjection {

    private UUID createdBy;
}
