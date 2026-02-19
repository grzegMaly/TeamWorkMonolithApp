package com.mordiniaa.backend.events.user.listeners;

import com.mordiniaa.backend.events.user.events.UserCreatedEvent;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
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
public class UserMongoProjectionListener {

    private final UserRepository userRepository;
    private final UserRepresentationRepository userRepresentationRepository;
    private final CloudStorageServiceUtils cloudStorageServiceUtils;

    @Async
    @Transactional(readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserCreatedEvent event) {

        User user = userRepository.findById(event.userId())
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        UserRepresentation mongoUser = new UserRepresentation();
        mongoUser.setUsername(user.getUsername());
        mongoUser.setImageKey(user.getImageKey());
        mongoUser.setUserId(user.getUserId());
        userRepresentationRepository.save(mongoUser);

        log.info("Mongo projection created for user: {}", event.userId());
    }
}
