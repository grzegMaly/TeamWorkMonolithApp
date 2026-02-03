package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    boolean existsTeamByTeamIdAndManager_UserId(UUID teamId, UUID managerUserId);
}
