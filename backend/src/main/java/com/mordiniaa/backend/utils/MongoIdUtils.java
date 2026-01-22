package com.mordiniaa.backend.utils;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class MongoIdUtils {

    public ObjectId getObjectId(String id) {
        if (!ObjectId.isValid(id)) {
            throw new RuntimeException(); // TODO: Change in Exceptions Section
        }
        return new ObjectId(id);
    }
}
