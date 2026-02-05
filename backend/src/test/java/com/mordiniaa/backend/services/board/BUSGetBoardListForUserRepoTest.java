package com.mordiniaa.backend.services.board;

import com.mordiniaa.backend.dto.board.BoardShortDto;
import com.mordiniaa.backend.mappers.board.BoardMapper;
import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.TaskRepository;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.BoardAggregationRepositoryImpl;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.services.user.MongoUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
public class BUSGetBoardListForUserRepoTest {

    @Autowired
    private BoardUserService boardUserService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepresentationRepository userRepresentationRepository;

    private final UUID ownerId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();

    UserRepresentation user;

    private Board board1;
    private Board board2;

    @BeforeEach
    void setup() {

        user = new UserRepresentation();
        user.setUserId(ownerId);
        user.setUsername("Username");
        user.setImageUrl("https://random123.com");
        user = userRepresentationRepository.save(user);

        board1 = new Board();
        board1.setBoardName("Board 1");
        board1.setOwner(new BoardMember(ownerId));
        board1.setTeamId(teamId);
        board1.setUpdatedAt(Instant.now().minus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));
        board1 = boardRepository.save(board1);

        board2 = new Board();
        board2.setBoardName("Board 2");
        board2.setOwner(new BoardMember(ownerId));
        board2.setTeamId(teamId);
        board2.setUpdatedAt(Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));
        board2 = boardRepository.save(board2);

    }

    @AfterEach
    void clear() {
        boardRepository.deleteAll();
        taskRepository.deleteAll();
        userRepresentationRepository.deleteAll();
    }

    @Test
    @DisplayName("Get Board List For User Valid Test")
    void getBoardListForUserValidTest() {

        List<BoardShortDto> boards = boardUserService.getBoardListForUser(ownerId, teamId);
        assertFalse(boards.isEmpty());

        assertThat(boards)
                .extracting(BoardShortDto::getBoardName)
                .containsExactly(board2.getBoardName(), board1.getBoardName());
    }

    @Test
    @DisplayName("Get Board List For User Invalid Test")
    void getBoardListForUserInvalidTest() {
        assertThrows(RuntimeException.class, () -> boardUserService.getBoardListForUser(UUID.randomUUID(), teamId));
    }

    @Test
    @DisplayName("Get Board List For User Invalid Test")
    void getBoardListForUserInvalidTest2() {

        user.setDeleted(true);
        userRepresentationRepository.save(user);
        assertThrows(RuntimeException.class, () -> boardUserService.getBoardListForUser(UUID.randomUUID(), teamId));
    }
}
