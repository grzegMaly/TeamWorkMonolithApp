package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.mappers.task.TaskMapper;
import com.mordiniaa.backend.mappers.task.TaskMapperToDtoTest;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.task.Task;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardMembersOnly;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mongo.user.aggregation.UserReprCustomRepository;
import com.mordiniaa.backend.repositories.mongo.user.aggregation.UserReprCustomRepositoryImpl;
import com.mordiniaa.backend.request.task.AssignUsersRequest;
import com.mordiniaa.backend.request.task.PatchTaskDataRequest;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TaskManagementServiceMockitoTest {

    @InjectMocks
    private TaskManagementService taskManagementService;

    @Mock
    private TaskService taskService;

    @Mock
    private UserReprCustomRepository userReprCustomRepository;

    @Mock
    private MongoIdUtils mongoIdUtils;

    @Mock
    private BoardAggregationRepository boardAggregationRepository;

    @Mock
    private BoardUtils boardUtils;

    @Mock
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Update Task By Owner Valid Test")
    void updateTaskByOwnerValidTest() {

        TaskDetailsDTO dto = mock(TaskDetailsDTO.class);

        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(dto);

        UUID ownerId = UUID.randomUUID();
        ObjectId boardId = ObjectId.get();
        ObjectId taskId = ObjectId.get();
        PatchTaskDataRequest patchTaskDataRequest = new PatchTaskDataRequest();
        TaskDetailsDTO result = taskManagementService.updateTask(
                ownerId, boardId.toHexString(), taskId.toHexString(), patchTaskDataRequest
        );

        assertSame(dto, result);

        verify(taskService, times(1)).executeTaskOperation(
                eq(ownerId),
                eq(boardId.toHexString()),
                eq(taskId.toHexString()),
                any(),
                any()
        );
    }
}
