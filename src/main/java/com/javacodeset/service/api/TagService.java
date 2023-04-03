package com.javacodeset.service.api;

import com.javacodeset.dto.TagDto;
import com.javacodeset.entity.TagEntity;

import java.util.List;
import java.util.UUID;

public interface TagService extends CrudService<TagEntity, TagDto, UUID> {
    TagEntity getTagByName(String tagName);
    Boolean existsTagByName(String tagName);
    List<TagEntity> getAllTagsByCodeBlockId(UUID codeBlockId);
    void addTagToCodeBlock(UUID tagId, UUID codeBlockId);
    void deleteTagFromCodeBlock(UUID tagId, UUID codeBlockId);
}
