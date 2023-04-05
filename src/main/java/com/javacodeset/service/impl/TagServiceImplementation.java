package com.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.TagDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.TagEntity;
import com.javacodeset.exception.BadRequestException;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.exception.ProhibitedOperationException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.TagRepository;
import com.javacodeset.service.api.TagService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagServiceImplementation implements TagService {

    private final TagRepository tagRepository;
    private final CodeBlockRepository codeBlockRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TagEntity create(TagDto tagDto) {
        tagDto.setName(tagDto.getName().trim().toLowerCase());

        if(Objects.equals(tagDto.getName(), ""))
            throw new BadRequestException("Name cannot be empty");

        if (!tagDto.getName().startsWith("#"))
            tagDto.setName("#" + tagDto.getName());

        if (tagRepository.existsByName(tagDto.getName()))
            return tagRepository.findByName(tagDto.getName()).orElseThrow(() ->
                    new NotFoundException(String.format("Tag with name '%s' does not exist", tagDto.getName())));

        TagEntity tag = modelMapper.map(tagDto, TagEntity.class);
        tag.setId(null);
        return tagRepository.save(tag);
    }

    @Override
    public TagEntity get(UUID tagId) {
        return tagRepository.findById(tagId).orElseThrow(() ->
                new NotFoundException(String.format("Tag with id '%s' does not exist", tagId)));
    }

    @Override
    public List<TagEntity> getAll() {
        return tagRepository.findAll();
    }

    @Override
    public TagEntity update(TagDto tagDto) {
        throw new ProhibitedOperationException("Update tag operation is prohibited, use 'create'");
    }

    @Override
    public void delete(UUID tagId) {
        if (tagRepository.existsById(tagId))
            tagRepository.deleteById(tagId);
    }

    @Override
    public TagEntity getTagByName(String tagName) {
        return tagRepository.findByName(tagName).orElseThrow(() ->
                new NotFoundException(String.format("Tag with name '%s' does not exist", tagName)));
    }

    @Override
    public Boolean existsTagByName(String tagName) {
        return tagRepository.existsByName(tagName);
    }

    @Override
    @Transactional
    public List<TagEntity> getAllTagsByCodeBlockId(UUID codeBlockId) {
        CodeBlockEntity codeBlock = codeBlockRepository.findById(codeBlockId).orElseThrow(() ->
                new NotFoundException(String.format("CodeBlock with id '%s' does not exist", codeBlockId)));
        return codeBlock.getTags().stream().toList();
    }

    @Override
    @Transactional
    public void addTagToCodeBlock(UUID tagId, UUID codeBlockId) {
        TagEntity tag = tagRepository.findById(tagId).orElseThrow(() ->
                new NotFoundException(String.format("Tag with id '%s' does not exist", tagId)));
        CodeBlockEntity codeBlock = codeBlockRepository.findById(codeBlockId).orElseThrow(() ->
                new NotFoundException(String.format("CodeBlock with id '%s' does not exist", codeBlockId)));

        tag.getCodeBlocks().add(codeBlock);
        codeBlock.getTags().add(tag);
        tagRepository.save(tag);
        codeBlockRepository.save(codeBlock);
    }

    @Override
    @Transactional
    public void deleteTagFromCodeBlock(UUID tagId, UUID codeBlockId) {
        TagEntity tag = tagRepository.findById(tagId).orElseThrow(() ->
                new NotFoundException(String.format("Tag with id '%s' does not exist", tagId)));
        CodeBlockEntity codeBlock = codeBlockRepository.findById(codeBlockId).orElseThrow(() ->
                new NotFoundException(String.format("CodeBlock with id '%s' does not exist", codeBlockId)));

        tag.getCodeBlocks().remove(codeBlock);
        codeBlock.getTags().remove(tag);
        tagRepository.save(tag);
        codeBlockRepository.save(codeBlock);
    }
}
