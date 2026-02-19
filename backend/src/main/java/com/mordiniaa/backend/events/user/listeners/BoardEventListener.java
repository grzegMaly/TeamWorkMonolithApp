package com.mordiniaa.backend.events.user.listeners;

import com.mordiniaa.backend.events.user.events.UserDeleteEvent;
import com.mordiniaa.backend.services.board.admin.BoardAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BoardEventListener {

    private final BoardAdminService boardAdminService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserDeleteEvent event) {
        boardAdminService.handleUserDeletion(event.userId());
    }
}
