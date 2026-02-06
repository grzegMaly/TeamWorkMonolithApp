package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import com.mordiniaa.backend.repositories.mysql.TeamRepository;
import com.mordiniaa.backend.request.board.BoardCreationRequest;
import com.mordiniaa.backend.services.user.MongoUserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardOwnerServiceCreateBoardMockTest {

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

    @Test
    void createBoardTest() {

        doNothing()
                .when(mongoUserService)
                .checkUserAvailability(any(UUID.class));

        UUID userId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        when(teamRepository.existsTeamByTeamIdAndManager_UserId(teamId, userId))
                .thenReturn(true);

        ObjectId boardId = ObjectId.get();
        Board board = new Board();
        board.setId(boardId);

        when(boardRepository.save(any(Board.class)))
                .thenReturn(board);

        BoardFull boardFull = mock(BoardFull.class);
        when(boardAggregationRepository.findBoardWithTasksByUserIdAndBoardIdAndTeamId(
                userId,
                boardId,
                teamId
        )).thenReturn(Optional.of(boardFull));

        BoardDetailsDto boardDetailsDto = mock(BoardDetailsDto.class);
        when(boardMapper.toDetailedDto(boardFull))
                .thenReturn(boardDetailsDto);

        String boardName = "BoardName";
        BoardCreationRequest boardCreationRequest = new BoardCreationRequest();
        boardCreationRequest.setTeamId(teamId);
        boardCreationRequest.setBoardName(boardName);

        BoardDetailsDto result = boardOwnerService.createBoard(userId, boardCreationRequest);

        assertSame(boardDetailsDto, result);
    }
}
