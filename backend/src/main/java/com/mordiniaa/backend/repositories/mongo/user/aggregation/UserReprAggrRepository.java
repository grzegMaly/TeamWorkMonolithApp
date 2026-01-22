package com.mordiniaa.backend.repositories.mongo.user.aggregation;

import java.util.Set;
import java.util.UUID;

public interface UserReprAggrRepository {
    boolean allUsersAvailable(Set<UUID> userIds);
}
