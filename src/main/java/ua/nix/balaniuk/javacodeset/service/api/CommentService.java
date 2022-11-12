package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.CommentDto;
import ua.nix.balaniuk.javacodeset.entity.CommentEntity;

import java.util.List;
import java.util.UUID;

public interface CommentService extends CrudOperationsService<CommentEntity, CommentDto, UUID> {
    List<CommentEntity> getAllCommentsByCodeBlockId(UUID codeBlockId);
}
