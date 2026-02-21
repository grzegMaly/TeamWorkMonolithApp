package com.mordiniaa.backend.controllers.secured.admin;

import com.mordiniaa.backend.request.team.TeamCreationRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/team")
public class TeamAdminController {

    public void createTeam(@Valid TeamCreationRequest teamCreationRequest) {

    }

    public void assignManagerToTeam(UUID userId, UUID teamId) {

    }

    public void removeManagerFromTeam(UUID teamId) {

    }

    public void archiveTeam(UUID teamId) {

    }

    public void addToTeam(UUID userId, UUID teamId) {

    }

    public void removeFromTeam(UUID userId, UUID teamId) {

    }
}
