package com.mordiniaa.backend.services.board.admin;

import com.mongodb.client.result.UpdateResult;
import com.mordiniaa.backend.services.board.owner.BoardOwnerService;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardOwnerServiceDeleteBoardMockTest {

    @InjectMocks
    private BoardOwnerService boardOwnerService;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private MongoIdUtils mongoIdUtils;

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    void deleteBoardTest() {

        UUID boardOwner = UUID.randomUUID();

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(boardOwner);

        ObjectId boardId = ObjectId.get();
        when(mongoIdUtils.getObjectId(anyString()))
                .thenReturn(boardId);

        UpdateResult updateResult = UpdateResult.acknowledged(1, 1L, null);
        when(mongoTemplate.updateFirst(
                any(),
                any(),
                any(Class.class)
        )).thenReturn(updateResult);

        assertDoesNotThrow(() -> boardOwnerService.deleteBoard(boardOwner, boardId.toHexString()));
    }
}
