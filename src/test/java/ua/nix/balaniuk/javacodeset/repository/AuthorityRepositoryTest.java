package ua.nix.balaniuk.javacodeset.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    public void findByName_authorityEntityExist_shodReturnPresentOptionalOfAuthorityEntity() {
        AuthorityEntity authorityEntity = EntityExampleStorage.getAuthorityEntity();
        authorityRepository.save(authorityEntity);

        Optional<AuthorityEntity> actual = authorityRepository.findByName(authorityEntity.getName());

        assertTrue(actual.isPresent());
    }

    @Test
    public void findByName_authorityEntityNotExist_shodReturnNotPresentOptionalOfAuthorityEntity() {
        Optional<AuthorityEntity> actual = authorityRepository.findByName("ROLE_SOME");
        assertFalse(actual.isPresent());
    }
}
