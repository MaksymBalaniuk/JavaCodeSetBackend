package ua.nix.balaniuk.javacodeset.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.CommentEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeBlockRepository codeBlockRepository;

    @Test
    public void findAllByCodeBlockIdOrderByUpdatedDesc_commentEntityExist_shodReturnNotEmptyCommentEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);
        CommentEntity commentEntity1 = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);
        CommentEntity commentEntity2 = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);
        commentRepository.save(commentEntity1);
        commentRepository.save(commentEntity2);

        List<CommentEntity> actual =
                commentRepository.findAllByCodeBlockIdOrderByUpdatedDesc(codeBlockEntity.getId());

        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getUpdated()).isAfterOrEqualTo(actual.get(1).getUpdated());
    }

    @Test
    public void findAllByCodeBlockIdOrderByUpdatedDesc_commentEntityNotExist_shodReturnEmptyCommentEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);

        List<CommentEntity> actual =
                commentRepository.findAllByCodeBlockIdOrderByUpdatedDesc(codeBlockEntity.getId());

        assertTrue(actual.isEmpty());
    }
}
