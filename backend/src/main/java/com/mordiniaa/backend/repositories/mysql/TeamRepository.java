package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    boolean existsTeamByTeamIdAndManager_UserId(UUID teamId, UUID managerUserId);

    @Query("select count(u) > 0 from Team t join t.teamMembers u where t.teamId = :teamid and u.userId = :userId")
    boolean existsUserInTeam(UUID teamId, UUID userId);
}
