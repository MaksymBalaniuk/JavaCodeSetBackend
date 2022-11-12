package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.CodeBlockDto;
import ua.nix.balaniuk.javacodeset.dto.filter.FilterCodeBlockDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;

import java.util.List;
import java.util.UUID;

public interface CodeBlockService extends CrudOperationsService<CodeBlockEntity, CodeBlockDto, UUID> {
    List<CodeBlockEntity> getAllCodeBlocksByUserId(UUID userId);
    List<CodeBlockEntity> getAllCodeBlocksByType(CodeBlockType codeBlockType);
    List<CodeBlockEntity> getAllFilteredCodeBlocks(FilterCodeBlockDto filterCodeBlockDto);
    List<CodeBlockEntity> getAllFilteredCodeBlocksByUserId(UUID userId, FilterCodeBlockDto filterCodeBlockDto);
    List<CodeBlockEntity> getAllFilteredCodeBlocksByUserIdAndEstimateType(
            UUID userId, EstimateType estimateType, FilterCodeBlockDto filterCodeBlockDto);
}
