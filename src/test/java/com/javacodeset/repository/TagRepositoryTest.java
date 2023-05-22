package com.javacodeset.repository;

import com.javacodeset.entity.TagEntity;
import com.javacodeset.example.EntityExampleStorage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void findByName_tagEntityExist_shodReturnPresentOptionalOfTagEntity() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        tagRepository.save(tagEntity);

        Optional<TagEntity> actual = tagRepository.findByName(tagEntity.getName());

        assertTrue(actual.isPresent());
    }

    @Test
    public void findByName_tagEntityNotExist_shodReturnNotPresentOptionalOfUserEntity() {
        Optional<TagEntity> actual = tagRepository.findByName("#stream");
        assertFalse(actual.isPresent());
    }

    @Test
    public void existsByName_tagEntityExist_shodReturnTrue() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        tagRepository.save(tagEntity);

        Boolean actual = tagRepository.existsByName(tagEntity.getName());

        assertTrue(actual);
    }

    @Test
    public void existsByName_tagEntityNotExist_shodReturnFalse() {
        Boolean actual = tagRepository.existsByName("#stream");
        assertFalse(actual);
    }
}
