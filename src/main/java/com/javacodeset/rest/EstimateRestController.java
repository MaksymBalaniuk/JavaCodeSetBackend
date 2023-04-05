package com.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javacodeset.dto.EstimateDto;
import com.javacodeset.service.api.EstimateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
public class EstimateRestController {

    private final EstimateService estimateService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public EstimateDto createEstimate(@RequestBody EstimateDto estimateDto) {
        return modelMapper.map(estimateService.create(estimateDto), EstimateDto.class);
    }

    @GetMapping("/get/{estimateId}")
    public EstimateDto getEstimateById(@PathVariable UUID estimateId) {
        return modelMapper.map(estimateService.get(estimateId), EstimateDto.class);
    }

    @PatchMapping("/update")
    public EstimateDto updateEstimate(@RequestBody EstimateDto estimateDto) {
        return modelMapper.map(estimateService.update(estimateDto), EstimateDto.class);
    }

    @DeleteMapping("/delete/{estimateId}")
    public ResponseEntity<Object> deleteEstimateById(@PathVariable UUID estimateId) {
        estimateService.delete(estimateId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-all/by-block-id/{codeBlockId}")
    public List<EstimateDto> getAllEstimatesByCodeBlockId(@PathVariable UUID codeBlockId) {
        return estimateService.getAllEstimatesByCodeBlockId(codeBlockId).stream()
                .map(estimateEntity -> modelMapper.map(estimateEntity, EstimateDto.class)).toList();
    }
}
