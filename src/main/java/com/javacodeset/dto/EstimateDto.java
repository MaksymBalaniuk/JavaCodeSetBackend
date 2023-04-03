package com.javacodeset.dto;

import lombok.Data;
import com.javacodeset.enumeration.EstimateType;

import java.util.UUID;

@Data
public class EstimateDto {
    private UUID id;
    private EstimateType type;
    private UUID userId;
    private UUID codeBlockId;
}
