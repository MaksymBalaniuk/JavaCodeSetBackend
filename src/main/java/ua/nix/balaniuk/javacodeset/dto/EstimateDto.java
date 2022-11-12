package ua.nix.balaniuk.javacodeset.dto;

import lombok.Data;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;

import java.util.UUID;

@Data
public class EstimateDto {
    private UUID id;
    private EstimateType type;
    private UUID userId;
    private UUID codeBlockId;
}
