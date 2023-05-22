package com.javacodeset.repository;

import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.EstimateEntity;
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
public class EstimateRepositoryTest {

    @Autowired
    private EstimateRepository estimateRepository;

    @Autowired
    private CodeBlockRepository codeBlockRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findAllByCodeBlockId_estimateEntityExist_shodReturnNotEmptyEstimateEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        estimateRepository.save(estimateEntity);
        List<EstimateEntity> expected = List.of(estimateEntity);

        List<EstimateEntity> actual = estimateRepository.findAllByCodeBlockId(codeBlockEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByCodeBlockId_estimateEntityNotExist_shodReturnEmptyEstimateEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);

        List<EstimateEntity> actual = estimateRepository.findAllByCodeBlockId(codeBlockEntity.getId());

        assertTrue(actual.isEmpty());
    }
}
