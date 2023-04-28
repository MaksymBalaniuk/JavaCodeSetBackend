package com.javacodeset.service.api;

import com.javacodeset.dto.ShareDto;
import com.javacodeset.entity.ShareEntity;

import java.util.List;
import java.util.UUID;

public interface ShareService extends CrudService<ShareEntity, ShareDto, UUID> {
    List<ShareEntity> getAllSharesToUserId(UUID userId);
    List<ShareEntity> getAllSharesFromUserId(UUID userId);
    List<ShareEntity> getAllSharesOfCodeBlockId(UUID codeBlockId);
}
