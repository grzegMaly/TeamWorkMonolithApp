package com.mordiniaa.backend.services.notes.user;

import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MongoUserService {

    private final UserRepresentationRepository userRepresentationRepository;

    public void checkUserAvailability(UUID userId) {
        boolean result = userRepresentationRepository.existsUserRepresentationByUserIdAndDeletedFalse(userId);
        if (!result) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }
    }
}
