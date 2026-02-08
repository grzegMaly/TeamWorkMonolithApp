package com.mordiniaa.backend.services.team;

import com.mordiniaa.backend.models.team.Team;
import com.mordiniaa.backend.models.user.mysql.AppRole;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.request.team.TeamCreationRequest;
import com.mordiniaa.backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamAdminService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TeamService teamService;

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

    @Transactional
    //    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void assignManagerToTeam(UUID userId, UUID teamId) {

        User user = userService.getUser(userId);

        if (!user.getRole().getAppRole().equals(AppRole.ROLE_MANAGER))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        Team team = teamService.getTeam(teamId);

        if (team.getManager() != null) {
            User manager = team.getManager();
            if (manager.getUserId().equals(userId))
                // This manager Already Assigned
                throw new RuntimeException(); // TODO: Change In Exceptions Section
            // Other Manager Already Assigned
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }

        team.setManager(user);
        teamRepository.save(team);
    }

    @Transactional
    //    @PreAuthorize("hasRole('ADMIN')") TODO: In Future
    public void removeManagerFromTeam(UUID teamId) {

        Team team = teamService.getTeam(teamId);
        if (team.getManager() == null)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        team.removeManager();
        teamRepository.save(team);
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
