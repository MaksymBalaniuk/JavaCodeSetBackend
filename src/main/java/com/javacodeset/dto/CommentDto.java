package com.javacodeset.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CommentDto {
    private UUID id;
    private String comment;
    private Instant created;
    private Instant updated;
    private UUID userId;
    private UUID codeBlockId;
}
