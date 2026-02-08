package com.mordiniaa.backend.services.team;

import com.mordiniaa.backend.models.team.Team;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.request.team.TeamCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamAdminService {

    private final TeamRepository teamRepository;

    //Protected On Method Lvl For ADMIN
    @Transactional
//    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void createTeam(TeamCreationRequest teamCreationRequest) {

        String teamName = teamCreationRequest.getTeamName().trim();
        String lowerTeamName = teamName.toLowerCase();
        if (teamRepository.existsByTeamNameIgnoreCase(lowerTeamName))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Team team = new Team(lowerTeamName);
        team.setPresentationName(teamName);

        teamRepository.save(team);
    }

//    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void assignManagerToTeam() {

    }

//    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void removeManagerFromTeam() {

    }

//    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void archiveTeam() {

    }

//    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void addToTeam() {

    }

//    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void removeFromTeam() {

    }
}
