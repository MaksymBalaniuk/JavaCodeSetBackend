package ua.nix.balaniuk.javacodeset.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.EstimateEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    public void findAllByOrderByUpdatedDesc_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity1 = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CodeBlockEntity codeBlockEntity2 = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity1);
        codeBlockRepository.save(codeBlockEntity2);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByOrderByUpdatedDesc();

        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getUpdated()).isAfterOrEqualTo(actual.get(1).getUpdated());
    }

    @Test
    public void findAllByOrderByUpdatedDesc_codeBlockEntityNotExist_shodReturnEmptyCodeBlockEntityList() {
        List<CodeBlockEntity> actual = codeBlockRepository.findAllByOrderByUpdatedDesc();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllByUserId_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByUserId(userEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByUserId_codeBlockEntityNotExist_shodReturnEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByUserId(userEntity.getId());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllByUserIdOrderByUpdatedDesc_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity1 = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CodeBlockEntity codeBlockEntity2 = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity1);
        codeBlockRepository.save(codeBlockEntity2);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByUserIdOrderByUpdatedDesc(userEntity.getId());

        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getUpdated()).isAfterOrEqualTo(actual.get(1).getUpdated());
    }

    @Test
    public void findAllByUserIdOrderByUpdatedDesc_codeBlockEntityNotExist_shodReturnEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByUserIdOrderByUpdatedDesc(userEntity.getId());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllByType_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity1 = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity1.setType(CodeBlockType.PUBLIC);
        CodeBlockEntity codeBlockEntity2 = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity2.setType(CodeBlockType.HIDDEN);
        codeBlockRepository.save(codeBlockEntity1);
        codeBlockRepository.save(codeBlockEntity2);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity1);

        List<CodeBlockEntity> actual = codeBlockRepository.findAllByType(CodeBlockType.PUBLIC);

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByType_codeBlockEntityNotExist_shodReturnEmptyCodeBlockEntityList() {
        List<CodeBlockEntity> actual = codeBlockRepository.findAllByType(CodeBlockType.PUBLIC);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void findAllCodeBlocksByUserIdAndEstimateType_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
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
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);

        List<CodeBlockEntity> actual =
                codeBlockRepository.findAllCodeBlocksByUserIdAndEstimateType(userEntity.getId(), EstimateType.LIKE);

        assertTrue(actual.isEmpty());
    }
}

