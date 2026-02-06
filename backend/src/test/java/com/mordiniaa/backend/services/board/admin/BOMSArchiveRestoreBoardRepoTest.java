package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class BOMSArchiveRestoreBoardRepoTest {

    @Autowired
    private BoardOwnerManagementService managementService;

    @Autowired
    private UserRepresentationRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    private final UUID ownerId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();

    private UserRepresentation user;
    private BoardMember ownerMember;

    private Board board;

    @BeforeEach
    void setup() {

        user = new UserRepresentation();
        user.setUserId(ownerId);
        user.setUsername("Username");
        user.setImageUrl("imageUrl");
        user = userRepository.save(user);

        ownerMember = new BoardMember(ownerId);
        board = new Board();
        board.setBoardName("BoardName");
        board.setTeamId(teamId);
        board.setBoardName("BoardName");
        board.setOwner(ownerMember);
        board = boardRepository.save(board);
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("Archive Board Valid Test")
    void archiveBoardValidTest() {

        assertDoesNotThrow(() -> managementService.archiveBoard(ownerId, board.getId().toHexString()));
        Board updatedBoard = boardRepository.findById(board.getId())
                .orElseThrow();

        assertTrue(updatedBoard.isArchived());
    }

    @Test
    @DisplayName("Archive Board User Not Active Test")
    void archiveBoardUserNotActiveTest() {

        user.setDeleted(true);
        userRepository.save(user);

        assertThrows(RuntimeException.class, () -> managementService.archiveBoard(ownerId, board.getId().toHexString()));
    }

    @Test
    @DisplayName("Archive Board User Not Found Test")
    void archiveBoardUserNotFoundTest() {

        UUID userId = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> managementService.archiveBoard(userId, board.getId().toHexString()));
    }
}
