package com.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javacodeset.dto.CommentDto;
import com.javacodeset.service.api.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        return modelMapper.map(commentService.create(commentDto), CommentDto.class);
    }

    @GetMapping("/get/{commentId}")
    public CommentDto getCommentById(@PathVariable UUID commentId) {
        return modelMapper.map(commentService.get(commentId), CommentDto.class);
    }

    @PatchMapping("/update")
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        return modelMapper.map(commentService.update(commentDto), CommentDto.class);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Object> deleteCommentById(@PathVariable UUID commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-all/by-block-id/{codeBlockId}")
    public List<CommentDto> getAllCommentsByCodeBlockId(@PathVariable UUID codeBlockId) {
        return commentService.getAllCommentsByCodeBlockId(codeBlockId).stream()
                .map(commentEntity ->  modelMapper.map(commentEntity, CommentDto.class)).toList();
    }
}
