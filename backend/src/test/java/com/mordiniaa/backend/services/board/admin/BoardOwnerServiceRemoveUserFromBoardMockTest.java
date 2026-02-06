package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardOwnerServiceRemoveUserFromBoardMockTest {

    @InjectMocks
    private BoardOwnerService boardOwnerService;

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardAggregationRepositoryImpl boardAggregationRepository;

    @Mock
    private MongoIdUtils mongoIdUtils;
}
