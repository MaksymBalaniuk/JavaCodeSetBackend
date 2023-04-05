package com.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.EstimateDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.EstimateEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.EstimateRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.api.EstimateService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstimateServiceImplementation implements EstimateService {

    private final EstimateRepository estimateRepository;
    private final UserRepository userRepository;
    private final CodeBlockRepository codeBlockRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public EstimateEntity create(EstimateDto estimateDto) {
        UserEntity user = userRepository.findById(estimateDto.getUserId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "User with id '%s' does not exist", estimateDto.getUserId())));
        CodeBlockEntity codeBlock = codeBlockRepository.findById(estimateDto.getCodeBlockId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "CodeBlock with id '%s' does not exist", estimateDto.getCodeBlockId())));

        EstimateEntity estimate = modelMapper.map(estimateDto, EstimateEntity.class);
        estimate.setId(null);
        estimate.setUser(user);
        estimate.setCodeBlock(codeBlock);
        estimateRepository.save(estimate);
        user.getEstimates().add(estimate);
        userRepository.save(user);
        codeBlock.getEstimates().add(estimate);
        codeBlockRepository.save(codeBlock);
        return estimate;
    }

    @Override
    public EstimateEntity get(UUID estimateId) {
        return estimateRepository.findById(estimateId).orElseThrow(() ->
                new NotFoundException(String.format("Estimate with id '%s' does not exist", estimateId)));
    }

    @Override
    public List<EstimateEntity> getAll() {
        return estimateRepository.findAll();
    }

    @Override
    @Transactional
    public EstimateEntity update(EstimateDto estimateDto) {
        EstimateEntity estimate = estimateRepository.findById(estimateDto.getId()).orElseThrow(() ->
                new NotFoundException(String.format("Estimate with id '%s' does not exist", estimateDto.getId())));
        estimate.setType(estimateDto.getType());
        return estimateRepository.save(estimate);
    }

    @Override
    public void delete(UUID estimateId) {
        if (estimateRepository.existsById(estimateId))
            estimateRepository.deleteById(estimateId);
    }

    @Override
    public List<EstimateEntity> getAllEstimatesByCodeBlockId(UUID codeBlockId) {
        return estimateRepository.findAllByCodeBlockId(codeBlockId);
    }
}
