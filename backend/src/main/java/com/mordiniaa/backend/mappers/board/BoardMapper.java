package com.mordiniaa.backend.mappers.board;

import com.mordiniaa.backend.dto.board.BoardDetailsDto;
import com.mordiniaa.backend.dto.board.BoardShortDto;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.dto.user.UserDto;
import com.mordiniaa.backend.mappers.user.UserRepresentationMapper;
import com.mordiniaa.backend.models.board.BoardTemplate;
import com.mordiniaa.backend.repositories.mongo.board.aggregation.returnTypes.BoardFull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class BoardMapper {

    private final Executor boardMapperExecutor;
    private final UserRepresentationMapper userRepresentationMapper;

    public BoardMapper(
            @Qualifier("boardMapperExecutor") Executor boardMapperExecutor,
            UserRepresentationMapper userRepresentationMapper
    ) {
        this.boardMapperExecutor = boardMapperExecutor;
        this.userRepresentationMapper = userRepresentationMapper;
    }

    public BoardShortDto toShortDto(BoardTemplate board) {
        BoardShortDto dto = new BoardShortDto();
        dto.setBoardId(board.getId().toHexString());
        dto.setBoardName(board.getBoardName());
        return dto;
    }

    public BoardDetailsDto toDetailedDto(BoardFull board) {

        BoardDetailsDto dto = new BoardDetailsDto();
        dto.setBoardId(board.getId().toHexString());
        dto.setBoardName(board.getBoardName());

        CompletableFuture<List<BoardDetailsDto.TaskCategoryDTO>> categoriesFuture = CompletableFuture
                .supplyAsync(() -> board.getTaskCategories()
                        .stream()
                        .map(category -> {
                            BoardDetailsDto.TaskCategoryDTO tDto = new BoardDetailsDto.TaskCategoryDTO();
                            tDto.setCategoryName(category.getCategoryName());
                            tDto.setPosition(category.getPosition());
                            tDto.setCreatedAt(category.getCreatedAt());

                            List<TaskShortDto> shortTasks = category.getTasks().stream()
                                    .map(task -> {
                                        TaskShortDto shortDto = new TaskShortDto();
                                        shortDto.setId(task.getId().toHexString());
                                        shortDto.setCreatedBy(task.getCreatedBy());
                                        shortDto.setPositionInCategory(task.getPositionInCategory());
                                        shortDto.setTitle(task.getTitle());
                                        shortDto.setDescription(task.getDescription());
                                        shortDto.setTaskStatus(task.getTaskStatus());
                                        shortDto.setAssignedTo(task.getAssignedTo());
                                        shortDto.setDeadline(task.getDeadline());
                                        return shortDto;
                                    }).toList();
                            tDto.setTasks(shortTasks);

                            return tDto;
                        }).toList()
                , boardMapperExecutor);

        CompletableFuture<List<UserDto>> usersFuture = CompletableFuture
                .supplyAsync(() -> board.getMembers().stream()
                        .map(member -> {
                            UserDto userDto = new UserDto();

                            userDto.setUsername(member.getUsername());
                            userDto.setUserId(member.getUserId());
                            userDto.setImageUrl(member.getImageUrl());
                            return userDto;
                        }).toList()
                , boardMapperExecutor);

        UserDto owner = userRepresentationMapper.toDto(board.getOwner());
        dto.setOwner(owner);

        CompletableFuture.allOf(categoriesFuture, usersFuture).join();
        dto.setTaskCategories(categoriesFuture.join());
        dto.setMembers(usersFuture.join());
        return dto;
    }
}
