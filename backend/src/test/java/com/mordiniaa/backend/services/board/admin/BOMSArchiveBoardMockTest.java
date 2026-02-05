package com.mordiniaa.backend.services.board.admin;

import com.mongodb.client.result.UpdateResult;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BOMSArchiveBoardMockTest {

    @InjectMocks
    private BoardOwnerManagementService managementService;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private MongoIdUtils mongoIdUtils;

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("Archive Board Test")
    void archiveBoardTest() {

        UUID ownerId = UUID.randomUUID();
        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(ownerId);

        ObjectId boardId = ObjectId.get();
        when(mongoIdUtils.getObjectId(anyString()))
                .thenReturn(boardId);

        UpdateResult result = UpdateResult.acknowledged(1, 1L, null);
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), any(Class.class)))
                .thenReturn(result);

        assertDoesNotThrow(() -> managementService.archiveBoard(ownerId, boardId.toHexString()));
    }
}
