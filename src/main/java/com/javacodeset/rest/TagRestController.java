package com.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javacodeset.dto.TagDto;
import com.javacodeset.service.api.TagService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagRestController {

    private final TagService tagService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public TagDto createTag(@RequestBody TagDto tagDto) {
        return modelMapper.map(tagService.create(tagDto), TagDto.class);
    }

    @GetMapping("/get/{tagId}")
    public TagDto getTagById(@PathVariable UUID tagId) {
        return modelMapper.map(tagService.get(tagId), TagDto.class);
    }

    @GetMapping("/get-all/by-block-id/{codeBlockId}")
    public List<TagDto> getAllTagsByCodeBlockId(@PathVariable UUID codeBlockId) {
        return tagService.getAllTagsByCodeBlockId(codeBlockId).stream()
                .map(tagEntity -> modelMapper.map(tagEntity, TagDto.class)).toList();
    }

    @PostMapping("/add/tag-to-block/{tagId}/{codeBlockId}")
    public ResponseEntity<Object> addTagToCodeBlock(@PathVariable UUID tagId, @PathVariable UUID codeBlockId) {
        tagService.addTagToCodeBlock(tagId, codeBlockId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/tag-from-block/{tagId}/{codeBlockId}")
    public ResponseEntity<Object> deleteTagFromCodeBlock(@PathVariable UUID tagId, @PathVariable UUID codeBlockId) {
        tagService.deleteTagFromCodeBlock(tagId, codeBlockId);
        return ResponseEntity.noContent().build();
    }
}
