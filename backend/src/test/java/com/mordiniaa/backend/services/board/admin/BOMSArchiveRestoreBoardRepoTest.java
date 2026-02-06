package com.mordiniaa.backend.services.board.admin;

import com.mordiniaa.backend.models.board.Board;
import com.mordiniaa.backend.models.board.BoardMember;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.board.BoardRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import org.bson.types.ObjectId;
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

    private Board board;

    @BeforeEach
    void setup() {

        user = new UserRepresentation();
        user.setUserId(ownerId);
        user.setUsername("Username");
        user.setImageUrl("imageUrl");
        user = userRepository.save(user);

        BoardMember ownerMember = new BoardMember(ownerId);
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

    @Test
    @DisplayName("Archive Board Board Not Found Test")
    void archiveBoardBoardNotFoundTest() {

        String boardId = ObjectId.get().toHexString();
        assertThrows(RuntimeException.class, () -> managementService.archiveBoard(ownerId, boardId));
    }

    @Test
    @DisplayName("Archive Board Board Already Archived Test")
    void archiveBoardBoardAlreadyArchivedTest() {

        board.setArchived(true);
        boardRepository.save(board);
        assertThrows(RuntimeException.class, () -> managementService.archiveBoard(ownerId, board.getId().toHexString()));
    }

    @Test
    @DisplayName("Archive Board Board Deleted Test")
    void archiveBoardBoardDeletedTest() {

        board.setDeleted(true);
        boardRepository.save(board);
        assertThrows(RuntimeException.class, () -> managementService.archiveBoard(ownerId, board.getId().toHexString()));
    }

    @Test
    @DisplayName("Archive Board User Not Board Owner Test")
    void archiveBoardUserNotBoardOwnerTest() {

        UUID userId = UUID.randomUUID();
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUserId(userId);
        newUser.setUsername("Username");
        newUser.setImageUrl("imageUrl");
        userRepository.save(newUser);
        assertThrows(RuntimeException.class, () -> managementService.archiveBoard(userId, board.getId().toHexString()));
    }

    @Test
    @DisplayName("Restore Board Valid Test")
    void restoreBoardValidTest() {

        board.setArchived(true);
        boardRepository.save(board);
        assertDoesNotThrow(() -> managementService.restoreBoard(ownerId, board.getId().toHexString()));
        Board updatedBoard = boardRepository.findById(board.getId())
                .orElseThrow();

        assertFalse(updatedBoard.isArchived());
    }
}
