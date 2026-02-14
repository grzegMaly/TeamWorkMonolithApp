package com.mordiniaa.backend.mappers.team;

import com.mordiniaa.backend.dto.team.TeamDetailedDto;
import com.mordiniaa.backend.dto.team.TeamShortDto;
import com.mordiniaa.backend.models.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMapper {

    public TeamShortDto toShortDto(Team team) {

        return new TeamDetailedDto(
                team.getTeamId(),
                team.getPresentationName()
        );
    }
}
