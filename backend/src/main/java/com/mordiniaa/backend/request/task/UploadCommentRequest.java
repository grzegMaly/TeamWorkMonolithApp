package com.mordiniaa.backend.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UploadCommentRequest {

    private UUID commentId;

    @NotBlank
    @Size(min = 3, max = 256)
    private String comment;

    public void setContent(String comment) {
        this.comment = comment.trim();
    }
}
