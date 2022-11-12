package ua.nix.balaniuk.javacodeset.dto;

import lombok.Data;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;

import java.time.Instant;
import java.util.UUID;

@Data
public class CodeBlockDto {
    private UUID id;
    private String title;
    private String description;
    private String content;
    private CodeBlockType type;
    private Instant created;
    private Instant updated;
    private UUID userId;
}
