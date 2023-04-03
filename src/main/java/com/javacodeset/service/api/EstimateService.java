package com.javacodeset.service.api;

import com.javacodeset.dto.EstimateDto;
import com.javacodeset.entity.EstimateEntity;

import java.util.List;
import java.util.UUID;

public interface EstimateService extends CrudService<EstimateEntity, EstimateDto, UUID> {
    List<EstimateEntity> getAllEstimatesByCodeBlockId(UUID codeBlockId);
}
