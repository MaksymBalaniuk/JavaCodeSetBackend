package com.javacodeset.repository;

import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.EstimateEntity;
import com.javacodeset.entity.ShareEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.EstimateType;
import com.javacodeset.example.EntityExampleStorage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CodeBlockRepositoryTest {

    @Autowired
    private CodeBlockRepository codeBlockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstimateRepository estimateRepository;

    @Autowired
    private ShareRepository shareRepository;

    @Test
    public void findAllByUserId_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByUserId(userEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByUserId_codeBlockEntityNotExist_shodReturnEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByUserId(userEntity.getId());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllCodeBlocksByUserIdAndEstimateType_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        estimateEntity.setType(EstimateType.LIKE);
        estimateRepository.save(estimateEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        List<CodeBlockEntity> actual =
                codeBlockRepository.findAllCodeBlocksByUserIdAndEstimateType(userEntity.getId(), EstimateType.LIKE);

        assertEquals(expected, actual);
    }

    @Test
    public void findAllCodeBlocksByUserIdAndEstimateType_codeBlockEntityNotExist_shodReturnEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);

        List<CodeBlockEntity> actual =
                codeBlockRepository.findAllCodeBlocksByUserIdAndEstimateType(userEntity.getId(), EstimateType.LIKE);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllCodeBlocksSharedFromUserIdToUserId_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        userRepository.save(fromUserEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(fromUserEntity);
        codeBlockRepository.save(codeBlockEntity);
        ShareEntity shareEntity = EntityExampleStorage.getShareEntity(toUserEntity, fromUserEntity, codeBlockEntity);
        shareRepository.save(shareEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        List<CodeBlockEntity> actual =
                codeBlockRepository.findAllCodeBlocksSharedFromUserIdToUserId(
                        fromUserEntity.getId(), toUserEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllCodeBlocksSharedFromUserIdToUserId_codeBlockEntityNotExist_shodReturnEmptyCodeBlockEntityList() {
        UserEntity toUserEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(toUserEntity);
        UserEntity fromUserEntity = EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com");
        userRepository.save(fromUserEntity);

        List<CodeBlockEntity> actual =
                codeBlockRepository.findAllCodeBlocksSharedFromUserIdToUserId(
                        fromUserEntity.getId(), toUserEntity.getId());

        assertTrue(actual.isEmpty());
    }
}
