package com.mordiniaa.backend.task.serviceMock;

import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepository;
import com.mordiniaa.backend.repositories.mongo.user.aggregation.UserReprCustomRepository;
import com.mordiniaa.backend.services.task.TaskManagementService;
import com.mordiniaa.backend.services.task.TaskService;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.BoardUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TaskManagementServiceMockitoTest {

    @InjectMocks
    private TaskManagementService taskManagementService;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private MongoIdUtils mongoIdUtils;

    @Mock
    private BoardAggregationRepository boardAggregationRepository;

    @Mock
    private BoardUtils boardUtils;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private UserReprCustomRepository userReprCustomRepository;


}
