package com.mordiniaa.backend.services.task;

import com.mordiniaa.backend.dto.task.TaskDetailsDTO;
import com.mordiniaa.backend.dto.task.TaskShortDto;
import com.mordiniaa.backend.request.task.UpdateTaskPositionRequest;
import com.mordiniaa.backend.request.task.UploadCommentRequest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskActivityServiceMockitoTest {

    @InjectMocks
    private TaskActivityService taskActivityService;

    @Mock
    private TaskService taskService;

    @Test
    @DisplayName("Change Task Position Test")
    void changeTaskPositionTest() {

        UUID userId = UUID.randomUUID();
        ObjectId boardId = ObjectId.get();
        ObjectId taskId = ObjectId.get();
        String bId = boardId.toHexString();
        String tId = taskId.toHexString();
        UpdateTaskPositionRequest request = new UpdateTaskPositionRequest();

        TaskShortDto taskShortDto = mock(TaskShortDto.class);
        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(taskShortDto);

        TaskShortDto result = taskActivityService.changeTaskPosition(
                userId,
                bId,
                tId,
                request
        );

        assertSame(taskShortDto, result);

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(userId),
                        eq(bId),
                        eq(tId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }

    @Test
    @DisplayName("Change Task Position Throws Exception Test")
    void changeTaskPositionThrowsExceptionTest() {

        UUID userId = UUID.randomUUID();
        ObjectId boardId = ObjectId.get();
        ObjectId taskId = ObjectId.get();
        String bId = boardId.toHexString();
        String tId = taskId.toHexString();
        UpdateTaskPositionRequest request = new UpdateTaskPositionRequest();

        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> taskActivityService.changeTaskPosition(
                userId,
                bId,
                tId,
                request
        ));

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(userId),
                        eq(bId),
                        eq(tId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }

    @Test
    @DisplayName("Write Comment Valid Test")
    void writeCommentValidTest() {

        TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(taskDetailsDTO);

        UUID ownerId = UUID.randomUUID();
        String boardId = ObjectId.get().toHexString();
        String taskId = ObjectId.get().toHexString();
        UploadCommentRequest uploadCommentRequest = new UploadCommentRequest();

        TaskDetailsDTO result = taskActivityService.writeComment(
                ownerId, boardId, taskId, uploadCommentRequest
        );

        assertSame(taskDetailsDTO, result);

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(ownerId),
                        eq(boardId),
                        eq(taskId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }

    @Test
    @DisplayName("Write Comment Throws Exception Test")
    void writeCommentThrowsExceptionTest() {

        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenThrow(RuntimeException.class);

        UUID ownerId = UUID.randomUUID();
        String boardId = ObjectId.get().toHexString();
        String taskId = ObjectId.get().toHexString();
        UploadCommentRequest uploadCommentRequest = new UploadCommentRequest();

        assertThrows(RuntimeException.class, () -> taskActivityService.writeComment(
                ownerId, boardId, taskId, uploadCommentRequest
        ));

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(ownerId),
                        eq(boardId),
                        eq(taskId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }

    @Test
    @DisplayName("Update Comment Valid Test")
    void updateCommentValidTest() {

        TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(taskDetailsDTO);

        UUID ownerId = UUID.randomUUID();
        String boardId = ObjectId.get().toHexString();
        String taskId = ObjectId.get().toHexString();

        UUID commentId = UUID.randomUUID();
        UploadCommentRequest uploadCommentRequest = new UploadCommentRequest();
        uploadCommentRequest.setCommentId(commentId);

        TaskDetailsDTO result = taskActivityService.updateComment(
                ownerId,
                boardId,
                taskId,
                uploadCommentRequest
        );

        assertSame(taskDetailsDTO, result);

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(ownerId),
                        eq(boardId),
                        eq(taskId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }

    @Test
    @DisplayName("Update Comment Throws Exception Test")
    void updateCommentThrowsExceptionTest() {

        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenThrow(RuntimeException.class);

        UUID ownerId = UUID.randomUUID();
        String boardId = ObjectId.get().toHexString();
        String taskId = ObjectId.get().toHexString();

        UUID commentId = UUID.randomUUID();
        UploadCommentRequest uploadCommentRequest = new UploadCommentRequest();
        uploadCommentRequest.setCommentId(commentId);

        assertThrows(RuntimeException.class, () -> taskActivityService.updateComment(
                ownerId,
                boardId,
                taskId,
                uploadCommentRequest
        ));

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(ownerId),
                        eq(boardId),
                        eq(taskId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }

    @Test
    @DisplayName("Delete Comment Valid Test")
    void deleteCommentValidTest() {

        TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(taskDetailsDTO);

        UUID ownerId = UUID.randomUUID();
        String boardId = ObjectId.get().toHexString();
        String taskId = ObjectId.get().toHexString();
        UUID commentId = UUID.randomUUID();

        TaskDetailsDTO result = taskActivityService.deleteComment(
                ownerId, boardId, taskId, commentId
        );

        assertSame(taskDetailsDTO, result);

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(ownerId),
                        eq(boardId),
                        eq(taskId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }

    @Test
    @DisplayName("Delete Comment Throws Exception Test")
    void deleteCommentThrowsExceptionTest() {

        TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
        when(taskService.executeTaskOperation(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenThrow(RuntimeException.class);

        UUID ownerId = UUID.randomUUID();
        String boardId = ObjectId.get().toHexString();
        String taskId = ObjectId.get().toHexString();
        UUID commentId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> taskActivityService.deleteComment(
                ownerId, boardId, taskId, commentId
        ));

        verify(taskService, times(1))
                .executeTaskOperation(
                        eq(ownerId),
                        eq(boardId),
                        eq(taskId),
                        any(),
                        any()
                );

        verifyNoMoreInteractions(taskService);
    }
}
