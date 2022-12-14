package ua.nix.balaniuk.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nix.balaniuk.javacodeset.dto.CodeBlockDto;
import ua.nix.balaniuk.javacodeset.dto.filter.FilterCodeBlockDto;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;
import ua.nix.balaniuk.javacodeset.service.api.CodeBlockService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class CodeBlockRestController {

    private final CodeBlockService codeBlockService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public CodeBlockDto createCodeBlock(@RequestBody CodeBlockDto codeBlockDto) {
        return modelMapper.map(codeBlockService.create(codeBlockDto), CodeBlockDto.class);
    }

    @GetMapping("/get/{codeBlockId}")
    public CodeBlockDto getCodeBlockById(@PathVariable UUID codeBlockId) {
        return modelMapper.map(codeBlockService.get(codeBlockId), CodeBlockDto.class);
    }

    @PatchMapping("/update")
    public CodeBlockDto updateCodeBlock(@RequestBody CodeBlockDto codeBlockDto) {
        return modelMapper.map(codeBlockService.update(codeBlockDto), CodeBlockDto.class);
    }

    @DeleteMapping("/delete/{codeBlockId}")
    public ResponseEntity<Object> deleteCodeBlockById(@PathVariable UUID codeBlockId) {
        codeBlockService.delete(codeBlockId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-all/by-user-id/{userId}")
    public List<CodeBlockDto> getAllCodeBlocksByUserId(@PathVariable UUID userId) {
        return codeBlockService.getAllCodeBlocksByUserId(userId).stream()
                .map(codeBlockEntity -> modelMapper.map(codeBlockEntity, CodeBlockDto.class)).toList();
    }

    @GetMapping("/get-all/by-block-type/{codeBlockType}")
    public List<CodeBlockDto> getAllCodeBlocksByType(@PathVariable CodeBlockType codeBlockType) {
        return codeBlockService.getAllCodeBlocksByType(codeBlockType).stream()
                .map(codeBlockEntity -> modelMapper.map(codeBlockEntity, CodeBlockDto.class)).toList();
    }

    @PostMapping("/get-all/filtered")
    public List<CodeBlockDto> getAllFilteredCodeBlocks(@RequestBody FilterCodeBlockDto filterCodeBlockDto) {
        return codeBlockService.getAllFilteredCodeBlocks(filterCodeBlockDto).stream()
                .map(codeBlockEntity -> modelMapper.map(codeBlockEntity, CodeBlockDto.class)).toList();
    }

    @PostMapping("/get-all/by-user-id/{userId}/filtered")
    public List<CodeBlockDto> getAllFilteredCodeBlocksByUserId(
            @PathVariable UUID userId, @RequestBody FilterCodeBlockDto filterCodeBlockDto) {
        return codeBlockService.getAllFilteredCodeBlocksByUserId(userId, filterCodeBlockDto).stream()
                .map(codeBlockEntity -> modelMapper.map(codeBlockEntity, CodeBlockDto.class)).toList();
    }

    @PostMapping("/get-all/by-user-id-and-estimate-type/{userId}/{estimateType}/filtered")
    public List<CodeBlockDto> getAllFilteredCodeBlocksByUserIdAndEstimateType(
            @PathVariable UUID userId, @PathVariable EstimateType estimateType,
            @RequestBody FilterCodeBlockDto filterCodeBlockDto) {
        return codeBlockService.getAllFilteredCodeBlocksByUserIdAndEstimateType(
                userId, estimateType, filterCodeBlockDto)
                .stream()
                .map(codeBlockEntity -> modelMapper.map(codeBlockEntity, CodeBlockDto.class)).toList();
    }
}
