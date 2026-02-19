package com.mordiniaa.backend.events.user.listeners;

import com.mordiniaa.backend.events.user.events.UserCreatedEvent;
import com.mordiniaa.backend.utils.CloudStorageServiceUtils;
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
public class UserStorageProvisioningListener {

    private final CloudStorageServiceUtils cloudStorageServiceUtils;

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserCreatedEvent event) {

        cloudStorageServiceUtils.createNewStorageSafely(event.userId());
        log.info("Storage created for user: {}", event.userId());
    }
}
