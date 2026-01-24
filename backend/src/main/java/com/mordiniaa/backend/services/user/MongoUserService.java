package com.mordiniaa.backend.services.user;

import com.mordiniaa.backend.repositories.mongo.user.aggregation.UserReprCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MongoUserService {

    private final UserReprCustomRepository userReprCustomRepository;

    public void checkUserAvailability(UUID... userIds) {
        boolean result = userReprCustomRepository.allUsersAvailable(userIds);
        if (!result) {
            throw new RuntimeException(); //TODO: Change in Exceptions Section
        }
    }
}
