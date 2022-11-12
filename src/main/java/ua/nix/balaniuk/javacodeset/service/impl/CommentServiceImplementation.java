package ua.nix.balaniuk.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nix.balaniuk.javacodeset.dto.CommentDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.CommentEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.CommentRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.api.CommentService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImplementation implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CodeBlockRepository codeBlockRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CommentEntity create(CommentDto commentDto) {
        UserEntity user = userRepository.findById(commentDto.getUserId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "User with id '%s' does not exist", commentDto.getUserId())));
        CodeBlockEntity codeBlock = codeBlockRepository.findById(commentDto.getCodeBlockId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "CodeBlock with id '%s' does not exist", commentDto.getCodeBlockId())));

        CommentEntity comment = modelMapper.map(commentDto, CommentEntity.class);
        comment.setId(null);
        comment.setUser(user);
        comment.setCodeBlock(codeBlock);
        commentRepository.save(comment);
        user.getComments().add(comment);
        userRepository.save(user);
        codeBlock.getComments().add(comment);
        codeBlockRepository.save(codeBlock);
        return comment;
    }

    @Override
    public CommentEntity get(UUID commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id '%s' does not exist", commentId)));
    }

    @Override
    public List<CommentEntity> getAll() {
        return commentRepository.findAll();
    }

    @Override
    @Transactional
    public CommentEntity update(CommentDto commentDto) {
        CommentEntity comment = commentRepository.findById(commentDto.getId()).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id '%s' does not exist", commentDto.getId())));
        comment.setComment(commentDto.getComment());
        return commentRepository.save(comment);
    }

    @Override
    public void delete(UUID commentId) {
        if (commentRepository.existsById(commentId))
            commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentEntity> getAllCommentsByCodeBlockId(UUID codeBlockId) {
        return commentRepository.findAllByCodeBlockIdOrderByUpdatedDesc(codeBlockId);
    }
}
