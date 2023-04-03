package com.javacodeset.service.api;

import com.javacodeset.dto.CodeBlockDto;
import com.javacodeset.dto.filter.FilterCodeBlockDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.enumeration.EstimateType;

import java.util.List;
import java.util.UUID;

public interface CodeBlockService extends CrudService<CodeBlockEntity, CodeBlockDto, UUID> {
    List<CodeBlockEntity> getAllFilteredCodeBlocks(FilterCodeBlockDto filterCodeBlockDto);
    List<CodeBlockEntity> getAllFilteredCodeBlocksByUserId(UUID userId, FilterCodeBlockDto filterCodeBlockDto);
    List<CodeBlockEntity> getAllFilteredCodeBlocksByUserIdAndEstimateType(
            UUID userId, EstimateType estimateType, FilterCodeBlockDto filterCodeBlockDto);
}
