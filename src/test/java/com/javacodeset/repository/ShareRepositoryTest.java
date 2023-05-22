package com.javacodeset.repository;

import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.ShareEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.example.EntityExampleStorage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ShareRepositoryTest {

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeBlockRepository codeBlockRepository;

    @Test
    public void existsByToUserIdAndFromUserIdAndCodeBlockId_shareEntityExist_shodReturnTrue() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        userRepository.save(fromUserEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        codeBlockRepository.save(codeBlockEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        shareRepository.save(shareEntity);

        Boolean actual = shareRepository.existsByToUserIdAndFromUserIdAndCodeBlockId(
                toUserEntity.getId(), fromUserEntity.getId(), codeBlockEntity.getId());

        assertTrue(actual);
    }

    @Test
    public void existsByToUserIdAndFromUserIdAndCodeBlockId_shareEntityNotExist_shodReturnFalse() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        userRepository.save(fromUserEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        codeBlockRepository.save(codeBlockEntity);

        Boolean actual = shareRepository.existsByToUserIdAndFromUserIdAndCodeBlockId(
                toUserEntity.getId(), fromUserEntity.getId(), codeBlockEntity.getId());

        assertFalse(actual);
    }

    @Test
    public void findAllByToUserId_shareEntityExist_shodReturnNotEmptyShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        userRepository.save(fromUserEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        codeBlockRepository.save(codeBlockEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        shareRepository.save(shareEntity);
        List<ShareEntity> expected = List.of(shareEntity);

        List<ShareEntity> actual = shareRepository.findAllByToUserId(toUserEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByToUserId_shareEntityNotExist_shodReturnEmptyShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);

        List<ShareEntity> actual = shareRepository.findAllByToUserId(toUserEntity.getId());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllByFromUserId_shareEntityExist_shodReturnNotEmptyShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        userRepository.save(fromUserEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        codeBlockRepository.save(codeBlockEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        shareRepository.save(shareEntity);
        List<ShareEntity> expected = List.of(shareEntity);

        List<ShareEntity> actual = shareRepository.findAllByFromUserId(fromUserEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByFromUserId_shareEntityNotExist_shodReturnEmptyShareEntityList() {
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(fromUserEntity);

        List<ShareEntity> actual = shareRepository.findAllByFromUserId(fromUserEntity.getId());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllByCodeBlockId_shareEntityExist_shodReturnNotEmptyShareEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        userRepository.save(fromUserEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        codeBlockRepository.save(codeBlockEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        shareRepository.save(shareEntity);
        List<ShareEntity> expected = List.of(shareEntity);

        List<ShareEntity> actual = shareRepository.findAllByCodeBlockId(codeBlockEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByCodeBlockId_shareEntityNotExist_shodReturnEmptyShareEntityList() {
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(fromUserEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        codeBlockRepository.save(codeBlockEntity);

        List<ShareEntity> actual = shareRepository.findAllByCodeBlockId(codeBlockEntity.getId());

        assertTrue(actual.isEmpty());
    }
}
