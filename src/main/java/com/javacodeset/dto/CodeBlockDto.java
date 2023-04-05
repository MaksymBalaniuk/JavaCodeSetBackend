package com.javacodeset.dto;

import lombok.Data;
import com.javacodeset.enumeration.CodeBlockType;

import java.util.UUID;

@Data
public class CodeBlockDto {
    private UUID id;
    private String title;
    private String description;
    private String content;
    private CodeBlockType type;
    private Long created;
    private Long updated;
    private UUID userId;
}
