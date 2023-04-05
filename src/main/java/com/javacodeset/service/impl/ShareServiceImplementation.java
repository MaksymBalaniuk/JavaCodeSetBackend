package com.javacodeset.service.impl;

import com.javacodeset.dto.ShareDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.ShareEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.exception.BadRequestException;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.exception.ProhibitedOperationException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.ShareRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.api.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareServiceImplementation implements ShareService {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final CodeBlockRepository codeBlockRepository;

    @Override
    @Transactional
    public ShareEntity create(ShareDto shareDto) {
        UserEntity toUser = userRepository.findById(shareDto.getToUserId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "User with id '%s' does not exist", shareDto.getToUserId())));
        UserEntity fromUser = userRepository.findById(shareDto.getFromUserId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "User with id '%s' does not exist", shareDto.getFromUserId())));

        if (Objects.equals(toUser.getId(), fromUser.getId()))
            throw new BadRequestException("Users cannot be the same");

        CodeBlockEntity codeBlock = codeBlockRepository.findById(shareDto.getCodeBlockId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "CodeBlock with id '%s' does not exist", shareDto.getCodeBlockId())));

        ShareEntity share = new ShareEntity();
        share.setId(null);
        share.setToUser(toUser);
        share.setFromUser(fromUser);
        share.setCodeBlock(codeBlock);
        shareRepository.save(share);
        toUser.getSharesToUser().add(share);
        userRepository.save(toUser);
        fromUser.getSharesFromUser().add(share);
        userRepository.save(fromUser);
        codeBlock.getShares().add(share);
        codeBlockRepository.save(codeBlock);
        return share;
    }

    @Override
    public ShareEntity get(UUID shareId) {
        return shareRepository.findById(shareId).orElseThrow(() ->
                new NotFoundException(String.format("Share with id '%s' does not exist", shareId)));
    }

    @Override
    public List<ShareEntity> getAll() {
        return shareRepository.findAll();
    }

    @Override
    public ShareEntity update(ShareDto shareDto) {
        throw new ProhibitedOperationException("Update share operation is prohibited, use 'create'");
    }

    @Override
    public void delete(UUID shareId) {
        if (shareRepository.existsById(shareId))
            shareRepository.deleteById(shareId);
    }

    @Override
    public List<ShareEntity> getAllSharesToUserId(UUID userId) {
        return shareRepository.findAllSharesToUserId(userId);
    }
}
