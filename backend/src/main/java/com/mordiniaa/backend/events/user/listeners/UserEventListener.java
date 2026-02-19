package com.mordiniaa.backend.events.user.listeners;

import com.mordiniaa.backend.events.user.events.UserCreatedEvent;
import com.mordiniaa.backend.events.user.events.UserProfileImageChangedEvent;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepository userRepository;
    private final UserRepresentationRepository userRepresentationRepository;
    private final MongoTemplate mongoTemplate;

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

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserProfileImageChangedEvent event) {

        Query query = Query.query(
                Criteria.where("userId").is(event.userId())
        );
        Update update = new Update()
                .set("imageKey", event.imageKey());

        mongoTemplate.updateFirst(query, update, UserRepresentation.class);
    }
}
