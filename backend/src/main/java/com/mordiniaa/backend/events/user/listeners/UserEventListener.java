package com.mordiniaa.backend.events.user.listeners;

import com.mordiniaa.backend.events.user.events.UserCreatedEvent;
import com.mordiniaa.backend.events.user.events.UserProfileImageChangedEvent;
import com.mordiniaa.backend.services.user.MongoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final MongoUserService mongoUserService;

    @Async
    @Transactional(readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserCreatedEvent event) {
        mongoUserService.createUserRepresentation(event.userId());
        log.info("Mongo projection created for user: {}", event.userId());
    }

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserProfileImageChangedEvent event) {
        mongoUserService.setProfileImageKey(event.userId(), event.imageKey());
        log.info("Image Changed For User: {}", event.userId());
    }
}
