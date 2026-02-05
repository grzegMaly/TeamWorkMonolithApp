package com.mordiniaa.backend.services.board;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.dto.board.BoardShortDto;
import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import com.mordiniaa.backend.services.user.MongoUserService;
import com.mordiniaa.backend.utils.MongoIdUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardUserServiceMockTest {

    @InjectMocks
    private BoardUserService boardUserService;

    @Mock
    private MongoUserService mongoUserService;

    @Mock
    private BoardAggregationRepositoryImpl boardAggregationRepository;

    @Mock
    private MongoIdUtils mongoIdUtils;

    @Mock
    private BoardMapper boardMapper;

    private final UUID userId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();

    @Test
    @DisplayName("Get Board List For User")
    void getBoardListForUser() {

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(userId);

        Board board1 = new Board();
        board1.setId(ObjectId.get());
        board1.setBoardName("Board1");
        board1.setUpdatedAt(Instant.now().minus(3, ChronoUnit.DAYS));

        Board board2 = new Board();
        board2.setId(ObjectId.get());
        board2.setBoardName("Board2");
        board2.setUpdatedAt(Instant.now().minus(1, ChronoUnit.DAYS));

        Board board3 = new Board();
        board3.setId(ObjectId.get());
        board3.setBoardName("Board3");
        board3.setUpdatedAt(Instant.now().minus(2, ChronoUnit.DAYS));

        when(boardAggregationRepository.findAllBoardsForUserByUserIdAndTeamId(userId, teamId))
                .thenReturn(Set.of(board1, board2, board3));

        BoardShortDto dto1 = new BoardShortDto();
        dto1.setBoardId(board1.getId().toHexString());
        dto1.setBoardName(board1.getBoardName());

        BoardShortDto dto2 = new BoardShortDto();
        dto2.setBoardId(board2.getId().toHexString());
        dto2.setBoardName(board2.getBoardName());

        BoardShortDto dto3 = new BoardShortDto();
        dto3.setBoardId(board3.getId().toHexString());
        dto3.setBoardName(board3.getBoardName());

        when(boardMapper.toShortDto(board1)).thenReturn(dto1);
        when(boardMapper.toShortDto(board2)).thenReturn(dto2);
        when(boardMapper.toShortDto(board3)).thenReturn(dto3);

        List<BoardShortDto> result = boardUserService.getBoardListForUser(userId, teamId);
        assertThat(result)
                .extracting(BoardShortDto::getBoardName)
                .containsExactly(board2.getBoardName(), board3.getBoardName(), board1.getBoardName());

        verify(mongoUserService, times(1)).checkUserAvailability(userId);
        verify(boardAggregationRepository, times(1)).findAllBoardsForUserByUserIdAndTeamId(userId, teamId);
    }

    @Test
    @DisplayName("Get Board List For User Throws Exception")
    void getBoardListForUserThrowsException() {

        doThrow(RuntimeException.class)
                .when(mongoUserService)
                .checkUserAvailability(userId);

        assertThrows(RuntimeException.class, () -> boardUserService.getBoardListForUser(userId, teamId));
    }

    @Test
    @DisplayName("Get Board Details Test")
    void getBoardDetailsTest() {

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(userId);

        ObjectId boardId = ObjectId.get();
        when(mongoIdUtils.getObjectId(anyString()))
                .thenReturn(boardId);

        BoardFull board = mock(BoardFull.class);
        when(boardAggregationRepository.findBoardWithTasksByUserIdAndBoardIdAndTeamId(userId, boardId, teamId))
                .thenReturn(Optional.of(board));

        BoardDetailsDto boardDetailsDto = mock(BoardDetailsDto.class);
        when(boardMapper.toDetailedDto(board))
                .thenReturn(boardDetailsDto);

        BoardDetailsDto mockedBoard = boardUserService.getBoardDetails(userId, "", teamId);
        assertSame(boardDetailsDto, mockedBoard);

        verify(boardAggregationRepository, times(1))
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(userId, boardId, teamId);
    }

    @Test
    @DisplayName("Get Board Details Throws Exception Test")
    void getBoardDetailsThrowsExceptionTest1() {

        doThrow(RuntimeException.class)
                .when(mongoUserService)
                .checkUserAvailability(userId);

        assertThrows(RuntimeException.class, () -> boardUserService.getBoardDetails(userId, "", teamId));
        verifyNoMoreInteractions(mongoIdUtils);
        verifyNoMoreInteractions(boardAggregationRepository);
        verifyNoMoreInteractions(boardMapper);
    }

    @Test
    @DisplayName("Get Board Details Throws Exception Test")
    void getBoardDetailsThrowsExceptionTest2() {

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(userId);

        doThrow(RuntimeException.class)
                .when(mongoIdUtils)
                .getObjectId(anyString());

        assertThrows(RuntimeException.class, () -> boardUserService.getBoardDetails(userId, "", teamId));
        verifyNoMoreInteractions(boardAggregationRepository);
        verifyNoMoreInteractions(boardMapper);
    }

    @Test
    @DisplayName("Get Board Details Throws Exception Test")
    void getBoardDetailsThrowsExceptionTest3() {

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(userId);

        when(mongoIdUtils.getObjectId(anyString()))
                .thenReturn(ObjectId.get());

        doThrow(RuntimeException.class)
                .when(boardAggregationRepository)
                .findBoardWithTasksByUserIdAndBoardIdAndTeamId(eq(userId), any(ObjectId.class), eq(teamId));

        assertThrows(RuntimeException.class, () -> boardUserService.getBoardDetails(userId, "", teamId));
        verifyNoMoreInteractions(boardMapper);
    }
}
