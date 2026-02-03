package com.mordiniaa.backend.request.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BoardCreationRequest {

    private UUID teamId;
    private String boardName;
}
