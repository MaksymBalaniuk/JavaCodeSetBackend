package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.EstimateDto;
import ua.nix.balaniuk.javacodeset.entity.EstimateEntity;

import java.util.List;
import java.util.UUID;

public interface EstimateService extends CrudOperationsService<EstimateEntity, EstimateDto, UUID> {
    List<EstimateEntity> getAllByCodeBlockId(UUID codeBlockId);
}
