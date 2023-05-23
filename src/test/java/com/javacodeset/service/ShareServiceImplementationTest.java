package com.javacodeset.service;

import com.javacodeset.dto.ShareDto;
import com.javacodeset.entity.*;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.exception.ProhibitedOperationException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.ShareRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.impl.ShareServiceImplementation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShareServiceImplementationTest {

    @Mock
    private ShareRepository shareRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CodeBlockRepository codeBlockRepository;

    @InjectMocks
    private ShareServiceImplementation shareService;

    private static final UUID TEST_SHARE_UUID = UUID.randomUUID();
    private static final UUID TEST_FROM_USER_UUID = UUID.randomUUID();
    private static final UUID TEST_TO_USER_UUID = UUID.randomUUID();
    private static final UUID TEST_CODE_BLOCK_UUID = UUID.randomUUID();

    @Test
    public void create_validData_shouldReturnSavedShareEntity() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        toUserEntity.setId(TEST_TO_USER_UUID);
        fromUserEntity.setId(TEST_FROM_USER_UUID);
        codeBlockEntity.setId(TEST_CODE_BLOCK_UUID);
        ShareDto shareDto = DtoExampleStorage.getShareDto(
                TEST_TO_USER_UUID, TEST_FROM_USER_UUID, TEST_CODE_BLOCK_UUID);

        given(userRepository.findById(TEST_TO_USER_UUID)).willReturn(Optional.of(toUserEntity));
        given(userRepository.findById(TEST_FROM_USER_UUID)).willReturn(Optional.of(fromUserEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));
        given(shareRepository.existsByToUserIdAndFromUserIdAndCodeBlockId(
                TEST_TO_USER_UUID, TEST_FROM_USER_UUID, TEST_CODE_BLOCK_UUID)).willReturn(false);
        given(userRepository.save(toUserEntity)).willReturn(toUserEntity);
        given(userRepository.save(fromUserEntity)).willReturn(fromUserEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);
        given(shareRepository.save(any(ShareEntity.class))).willReturn(new ShareEntity());

        ShareEntity actual = shareService.create(shareDto);

        assertNotNull(actual);

        verify(shareRepository).save(any(ShareEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidToUserId_shouldThrowException() {
        ShareDto shareDto = DtoExampleStorage.getShareDto(
                TEST_TO_USER_UUID, TEST_FROM_USER_UUID, TEST_CODE_BLOCK_UUID);
        given(userRepository.findById(TEST_TO_USER_UUID)).willReturn(Optional.empty());
        shareService.create(shareDto);
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidFromUserId_shouldThrowException() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        ShareDto shareDto = DtoExampleStorage.getShareDto(
                TEST_TO_USER_UUID, TEST_FROM_USER_UUID, TEST_CODE_BLOCK_UUID);

        given(userRepository.findById(TEST_TO_USER_UUID)).willReturn(Optional.of(toUserEntity));
        given(userRepository.findById(TEST_FROM_USER_UUID)).willReturn(Optional.empty());

        shareService.create(shareDto);
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidCodeBlockId_shouldThrowException() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        ShareDto shareDto = DtoExampleStorage.getShareDto(
                TEST_TO_USER_UUID, TEST_FROM_USER_UUID, TEST_CODE_BLOCK_UUID);

        given(userRepository.findById(TEST_TO_USER_UUID)).willReturn(Optional.of(toUserEntity));
        given(userRepository.findById(TEST_FROM_USER_UUID)).willReturn(Optional.of(fromUserEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());

        shareService.create(shareDto);
    }

    @Test
    public void get_validId_shouldReturnShareEntity() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);

        given(shareRepository.findById(TEST_SHARE_UUID)).willReturn(Optional.of(shareEntity));

        ShareEntity actual = shareService.get(TEST_SHARE_UUID);

        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shouldThrowException() {
        given(shareRepository.findById(TEST_SHARE_UUID)).willReturn(Optional.empty());
        shareService.get(TEST_SHARE_UUID);
    }

    @Test
    public void getAll_shouldReturnShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        List<ShareEntity> expected = List.of(shareEntity);

        given(shareRepository.findAll()).willReturn(expected);

        List<ShareEntity> actual = shareService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test(expected = ProhibitedOperationException.class)
    public void update_shouldThrowException() {
        ShareDto shareDto = DtoExampleStorage.getShareDto(
                TEST_TO_USER_UUID, TEST_FROM_USER_UUID, TEST_CODE_BLOCK_UUID);
        shareService.update(shareDto);
    }

    @Test
    public void delete_shareEntityExist_shouldDeleteShareEntity() {
        given(shareRepository.existsById(TEST_SHARE_UUID)).willReturn(true);
        willDoNothing().given(shareRepository).deleteById(TEST_SHARE_UUID);
        shareService.delete(TEST_SHARE_UUID);
        verify(shareRepository).deleteById(any(UUID.class));
    }

    @Test
    public void getAllSharesToUserId_shareEntityExist_shouldReturnNotEmptyShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        List<ShareEntity> expected = List.of(shareEntity);

        given(shareRepository.findAllByToUserId(TEST_TO_USER_UUID)).willReturn(expected);

        List<ShareEntity> actual = shareService.getAllSharesToUserId(TEST_TO_USER_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllSharesFromUserId_shareEntityExist_shouldReturnNotEmptyShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        List<ShareEntity> expected = List.of(shareEntity);

        given(shareRepository.findAllByFromUserId(TEST_FROM_USER_UUID)).willReturn(expected);

        List<ShareEntity> actual = shareService.getAllSharesFromUserId(TEST_FROM_USER_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllSharesOfCodeBlockId_shareEntityExist_shouldReturnNotEmptyShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        List<ShareEntity> expected = List.of(shareEntity);

        given(shareRepository.findAllByCodeBlockId(TEST_CODE_BLOCK_UUID)).willReturn(expected);

        List<ShareEntity> actual = shareService.getAllSharesOfCodeBlockId(TEST_CODE_BLOCK_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }
}
