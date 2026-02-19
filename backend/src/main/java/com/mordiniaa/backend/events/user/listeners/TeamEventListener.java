package com.mordiniaa.backend.events.user.listeners;

import com.mordiniaa.backend.events.user.events.UserDeleteEvent;
import com.mordiniaa.backend.services.team.TeamAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TeamEventListener {

    private final TeamAdminService teamAdminService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserDeleteEvent event) {
        teamAdminService.removeFromTeamByEvent(event.userId());
    }
}
