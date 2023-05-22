package com.javacodeset.repository;

import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.CommentEntity;
import com.javacodeset.entity.UserEntity;
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
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeBlockRepository codeBlockRepository;

    @Test
    public void findAllByCodeBlockId_commentEntityExist_shouldReturnNotEmptyCommentEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);
        CommentEntity commentEntity = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);
        commentRepository.save(commentEntity);
        List<CommentEntity> expected = List.of(commentEntity);

        List<CommentEntity> actual =
                commentRepository.findAllByCodeBlockId(codeBlockEntity.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByCodeBlockId_commentEntityNotExist_shouldReturnEmptyCommentEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockRepository.save(codeBlockEntity);

        List<CommentEntity> actual =
                commentRepository.findAllByCodeBlockId(codeBlockEntity.getId());

        assertTrue(actual.isEmpty());
    }
}
