package com.javacodeset.service.api;

import com.javacodeset.dto.CommentDto;
import com.javacodeset.entity.CommentEntity;

import java.util.List;
import java.util.UUID;

public interface CommentService extends CrudService<CommentEntity, CommentDto, UUID> {
    List<CommentEntity> getAllCommentsByCodeBlockId(UUID codeBlockId);
}
