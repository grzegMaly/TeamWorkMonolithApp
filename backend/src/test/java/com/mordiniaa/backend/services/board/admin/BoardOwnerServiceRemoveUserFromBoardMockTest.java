package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import com.mordiniaa.backend.services.board.owner.BoardOwnerService;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardOwnerServiceRemoveUserFromBoardMockTest {

    @InjectMocks
    private BoardOwnerService boardOwnerService;

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardAggregationRepositoryImpl boardAggregationRepository;

    @Mock
    private MongoIdUtils mongoIdUtils;

    @Test
    void removeUserFromBoard() {

        UUID ownerId = UUID.randomUUID();

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(ownerId);

        ObjectId boardId = ObjectId.get();
        when(mongoIdUtils.getObjectId(anyString()))
                .thenReturn(boardId);

        Board board = mock(Board.class);
        when(boardAggregationRepository.findFullBoardByIdAndOwner(
                boardId,
                ownerId
        )).thenReturn(Optional.of(board));

        UUID memberId = UUID.randomUUID();
        doNothing()
                .when(board)
                .removeMember(memberId);

        Board savedBoard = mock(Board.class);
        when(boardRepository.save(board))
                .thenReturn(savedBoard);

        BoardFull boardFull = mock(BoardFull.class);

        UUID teamId = UUID.randomUUID();
        when(board.getTeamId())
                .thenReturn(teamId);

        when(boardAggregationRepository.findBoardWithTasksByUserIdAndBoardIdAndTeamId(
                ownerId,
                boardId,
                teamId
        )).thenReturn(Optional.of(boardFull));

        BoardDetailsDto boardDetailsDto = mock(BoardDetailsDto.class);
        when(boardMapper.toDetailedDto(boardFull))
                .thenReturn(boardDetailsDto);

        BoardDetailsDto result = boardOwnerService.removeUserFromBoard(ownerId, memberId, boardId.toHexString());
        assertSame(result, boardDetailsDto);
    }
}
