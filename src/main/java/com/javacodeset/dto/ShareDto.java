package com.javacodeset.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ShareDto {
    private UUID id;
    private UUID toUserId;
    private UUID fromUserId;
    private UUID codeBlockId;
}
