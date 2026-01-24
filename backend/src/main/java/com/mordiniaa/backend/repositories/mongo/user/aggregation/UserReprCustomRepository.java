package com.mordiniaa.backend.repositories.mongo.user.aggregation;

import java.util.Set;
import java.util.UUID;

public interface UserReprCustomRepository {
    boolean allUsersAvailable(UUID... userIds);
    boolean allUsersAvailable(Set<UUID> userIds);
}
