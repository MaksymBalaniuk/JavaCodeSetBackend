package com.javacodeset.rest;

import com.javacodeset.dto.ShareDto;
import com.javacodeset.service.api.ShareService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareRestController {

    private final ShareService shareService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public ShareDto createShare(@RequestBody ShareDto shareDto) {
        return modelMapper.map(shareService.create(shareDto), ShareDto.class);
    }

    @GetMapping("/get/{shareId}")
    public ShareDto getShareById(@PathVariable UUID shareId) {
        return modelMapper.map(shareService.get(shareId), ShareDto.class);
    }

    @GetMapping("/get-all/to-user/{userId}")
    public List<ShareDto> getAllSharesToUserId(@PathVariable UUID userId) {
        return shareService.getAllSharesToUserId(userId).stream()
                .map(shareEntity -> modelMapper.map(shareEntity, ShareDto.class)).toList();
    }

    @GetMapping("/get-all/from-user/{userId}")
    public List<ShareDto> getAllSharesFromUserId(@PathVariable UUID userId) {
        return shareService.getAllSharesFromUserId(userId).stream()
                .map(shareEntity -> modelMapper.map(shareEntity, ShareDto.class)).toList();
    }
}
