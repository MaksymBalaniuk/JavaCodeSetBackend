package com.javacodeset.service.impl;

import com.javacodeset.exception.BadRequestException;
import com.javacodeset.util.PremiumLimitsPolicy;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.CodeBlockDto;
import com.javacodeset.dto.filter.FilterCodeBlockDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.CodeBlockType;
import com.javacodeset.enumeration.EstimateType;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.api.CodeBlockService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CodeBlockServiceImplementation implements CodeBlockService {

    private final CodeBlockRepository codeBlockRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CodeBlockEntity create(CodeBlockDto codeBlockDto) {
        UserEntity user = userRepository.findById(codeBlockDto.getUserId()).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", codeBlockDto.getUserId())));

        if (user.getCodeBlocks().size() >=
                PremiumLimitsPolicy.getPremiumLimits(user.getPremium()).getCodeBlocksLimit())
            throw new BadRequestException(String.format(
                    "User with id '%s' exceeds premium limit of code blocks", user.getId()));

        CodeBlockEntity codeBlock = modelMapper.map(codeBlockDto, CodeBlockEntity.class);
        codeBlock.setId(null);
        codeBlock.setType(CodeBlockType.PRIVATE);
        codeBlock.setUser(user);
        codeBlockRepository.save(codeBlock);
        user.getCodeBlocks().add(codeBlock);
        userRepository.save(user);
        return codeBlock;
    }

    @Override
    public CodeBlockEntity get(UUID codeBlockId) {
        return codeBlockRepository.findById(codeBlockId).orElseThrow(() ->
                new NotFoundException(String.format("CodeBlock with id '%s' does not exist", codeBlockId)));
    }

    @Override
    public List<CodeBlockEntity> getAll() {
        return codeBlockRepository.findAll();
    }

    @Override
    @Transactional
    public CodeBlockEntity update(CodeBlockDto codeBlockDto) {
        codeBlockRepository.findById(codeBlockDto.getId()).orElseThrow(() ->
                new NotFoundException(String.format("CodeBlock with id '%s' does not exist", codeBlockDto.getId())));
        UserEntity user = userRepository.findById(codeBlockDto.getUserId()).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", codeBlockDto.getUserId())));

        CodeBlockEntity codeBlock = modelMapper.map(codeBlockDto, CodeBlockEntity.class);
        codeBlock.setUser(user);
        codeBlockRepository.save(codeBlock);
        user.getCodeBlocks().add(codeBlock);
        userRepository.save(user);
        return codeBlock;
    }

    @Override
    public void delete(UUID codeBlockId) {
        if (codeBlockRepository.existsById(codeBlockId))
            codeBlockRepository.deleteById(codeBlockId);
    }

    @Override
    @Transactional
    public List<CodeBlockEntity> getAllFilteredCodeBlocks(FilterCodeBlockDto filterCodeBlockDto) {
        return filterCodeBlocks(filterCodeBlockDto, codeBlockRepository.findAll());
    }

    @Override
    public List<CodeBlockEntity> getAllCodeBlocksByUserId(UUID userId) {
        return codeBlockRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public List<CodeBlockEntity> getAllFilteredCodeBlocksByUserId(
            UUID userId, FilterCodeBlockDto filterCodeBlockDto) {
        return filterCodeBlocks(filterCodeBlockDto, codeBlockRepository.findAllByUserId(userId));
    }

    @Override
    public List<CodeBlockEntity> getAllFilteredCodeBlocksByUserIdAndEstimateType(
            UUID userId, EstimateType estimateType, FilterCodeBlockDto filterCodeBlockDto) {
        return filterCodeBlocks(filterCodeBlockDto,
                codeBlockRepository.findAllCodeBlocksByUserIdAndEstimateType(userId, estimateType));
    }

    private List<CodeBlockEntity> filterCodeBlocks(FilterCodeBlockDto filterDto, List<CodeBlockEntity> source) {
        if (source.isEmpty())
            return source;

        List<CodeBlockEntity> typeFilteredSource;
        List<CodeBlockType> allowedTypes = Arrays.asList(filterDto.getAllowedTypes());

        if (Objects.equals(filterDto.getAllowedTypes().length, 0))
            return new ArrayList<>();
        else
            typeFilteredSource = source.stream()
                    .filter(codeBlockEntity -> allowedTypes.contains(codeBlockEntity.getType()))
                    .toList();

        String filterQuery = filterDto.getFilterQuery().trim().toLowerCase();

        if (Objects.equals(filterQuery, ""))
            return typeFilteredSource;

        return typeFilteredSource.stream()
                .filter(codeBlockEntity -> filterSimpleCodeBlock(filterDto, filterQuery, codeBlockEntity))
                .toList();
    }

    private boolean filterSimpleCodeBlock(
            FilterCodeBlockDto filterDto, String filterQuery, CodeBlockEntity codeBlock) {
        if (filterDto.getFilterTitle() && codeBlock.getTitle().toLowerCase().contains(filterQuery))
            return true;

        if (filterDto.getFilterDescription() && codeBlock.getDescription().toLowerCase().contains(filterQuery))
            return true;

        if (filterDto.getFilterContent() && codeBlock.getContent().toLowerCase().contains(filterQuery))
            return true;

        return filterDto.getFilterTags() &&
                codeBlock.getTags().stream().anyMatch(tagEntity -> tagEntity.getName().contains(filterQuery));
    }
}
