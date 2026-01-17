package com.mordiniaa.backend.dto.note;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NoteDto {

    @EqualsAndHashCode.Include
    private String id;
    private String title;
    private UUID ownerId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
