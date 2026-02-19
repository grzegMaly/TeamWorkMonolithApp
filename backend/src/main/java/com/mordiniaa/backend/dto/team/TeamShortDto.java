package com.mordiniaa.backend.dto.team;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamShortDto {

    private UUID teamId;
    private String presentationName;
}
