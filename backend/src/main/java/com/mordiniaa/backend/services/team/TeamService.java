package com.mordiniaa.backend.services.team;

import com.mordiniaa.backend.models.team.Team;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    Team getTeam(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
    }

    //Protected On Method Lvl
    public void getTeamsForManager() {

    }

    public void getTeamDetails() {

    }
}
