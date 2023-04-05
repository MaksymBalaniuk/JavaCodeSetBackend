package com.javacodeset.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentDto {
    private UUID id;
    private String comment;
    private Long created;
    private Long updated;
    private UUID userId;
    private UUID codeBlockId;
}
