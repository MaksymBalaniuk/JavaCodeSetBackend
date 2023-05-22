package com.javacodeset.repository;

import com.javacodeset.entity.AuthorityEntity;
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
public class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    public void findByName_authorityEntityExist_shouldReturnPresentOptionalOfAuthorityEntity() {
        AuthorityEntity authorityEntity = EntityExampleStorage.getAuthorityEntity("ROLE_SOME");
        authorityRepository.save(authorityEntity);

        Optional<AuthorityEntity> actual = authorityRepository.findByName(authorityEntity.getName());

        assertTrue(actual.isPresent());
    }

    @Test
    public void findByName_authorityEntityNotExist_shouldReturnNotPresentOptionalOfAuthorityEntity() {
        Optional<AuthorityEntity> actual = authorityRepository.findByName("ROLE_SOME");
        assertFalse(actual.isPresent());
    }
}
