package ua.nix.balaniuk.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nix.balaniuk.javacodeset.dto.CodeBlockDto;
import ua.nix.balaniuk.javacodeset.dto.filter.FilterCodeBlockDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.api.CodeBlockService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    public List<CodeBlockEntity> getAllCodeBlocksByUserId(UUID userId) {
        return codeBlockRepository.findAllByUserId(userId);
    }

    @Override
    public List<CodeBlockEntity> getAllCodeBlocksByType(CodeBlockType codeBlockType) {
        return codeBlockRepository.findAllByType(codeBlockType);
    }

    @Override
    @Transactional
    public List<CodeBlockEntity> getAllFilteredCodeBlocks(FilterCodeBlockDto filterCodeBlockDto) {
        return filterCodeBlocks(filterCodeBlockDto, codeBlockRepository.findAllByOrderByUpdatedDesc());
    }

    @Override
    @Transactional
    public List<CodeBlockEntity> getAllFilteredCodeBlocksByUserId(
            UUID userId, FilterCodeBlockDto filterCodeBlockDto) {
        return filterCodeBlocks(filterCodeBlockDto, codeBlockRepository.findAllByUserIdOrderByUpdatedDesc(userId));
    }

    @Override
    public List<CodeBlockEntity> getAllFilteredCodeBlocksByUserIdAndEstimateType(
            UUID userId, EstimateType estimateType, FilterCodeBlockDto filterCodeBlockDto) {
        return filterCodeBlocks(filterCodeBlockDto,
                codeBlockRepository.findAllCodeBlocksByUserIdAndEstimateType(userId, estimateType));
    }

    private List<CodeBlockEntity> filterCodeBlocks(
            FilterCodeBlockDto filterDto, List<CodeBlockEntity> source) {
        String filterQuery = filterDto.getFilterQuery().trim().toLowerCase();

        if (Objects.equals(filterQuery, "") || source.isEmpty())
            return source;

        return source.stream()
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
