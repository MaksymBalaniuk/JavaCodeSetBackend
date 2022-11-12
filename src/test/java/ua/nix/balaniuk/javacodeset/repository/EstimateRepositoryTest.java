package ua.nix.balaniuk.javacodeset.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.EstimateEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        List<EstimateEntity> actual = estimateRepository.findAllByCodeBlockId(codeBlockEntity.getId());

        assertFalse(actual.isEmpty());
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
