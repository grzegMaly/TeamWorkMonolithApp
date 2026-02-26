package com.mordiniaa.backend.events.refreshToken.listeners;

import com.mordiniaa.backend.events.refreshToken.events.DeactivateTokenEvent;
import com.mordiniaa.backend.events.refreshToken.events.RotateRefreshTokenEvent;
import com.mordiniaa.backend.security.service.token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RefreshTokenListener {

    private final RefreshTokenService refreshTokenService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void handle(DeactivateTokenEvent event) {
        refreshTokenService.deactivateToken(
                event.tokenId(),
                event.familyId(),
                event.revokeTime()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void handle(RotateRefreshTokenEvent event) {
        refreshTokenService.rotateToken(
                event.newTokenId(),
                event.oldTokenId(),
                event.revokedTime()
        );
    }
}
