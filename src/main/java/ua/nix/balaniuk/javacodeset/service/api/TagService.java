package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.TagDto;
import ua.nix.balaniuk.javacodeset.entity.TagEntity;

import java.util.List;
import java.util.UUID;

public interface TagService extends CrudOperationsService<TagEntity, TagDto, UUID> {
    TagEntity getByName(String tagName);
    Boolean existByName(String tagName);
    List<TagEntity> getAllTagsByCodeBlockId(UUID codeBlockId);
    void addTagToCodeBlock(UUID tagId, UUID codeBlockId);
    void deleteTagFromCodeBlock(UUID tagId, UUID codeBlockId);
}
