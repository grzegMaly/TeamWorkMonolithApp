package com.mordiniaa.backend.services.board;

import com.mordiniaa.backend.models.board.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardUserService {

    public List<Board> getBoardListForUser(UUID userId) {
        return null;
    }

    public Board getBoardDetails(UUID userId, String boardId) {
        return null;
    }
}
