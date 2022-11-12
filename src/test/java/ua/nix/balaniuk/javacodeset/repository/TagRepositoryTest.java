package ua.nix.balaniuk.javacodeset.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.entity.TagEntity;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;

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
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
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
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        tagRepository.save(tagEntity);

        boolean actual = tagRepository.existsByName(tagEntity.getName());

        assertTrue(actual);
    }

    @Test
    public void existsByName_tagEntityNotExist_shodReturnTrue() {
        boolean actual = tagRepository.existsByName("#stream");
        assertFalse(actual);
    }
}
