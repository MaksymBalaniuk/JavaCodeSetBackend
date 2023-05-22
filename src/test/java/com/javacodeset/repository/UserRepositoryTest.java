package com.javacodeset.repository;

import com.javacodeset.entity.UserEntity;
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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_userEntityExist_shouldReturnPresentOptionalOfUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);

        Optional<UserEntity> actual = userRepository.findByUsername(userEntity.getUsername());

        assertTrue(actual.isPresent());
    }

    @Test
    public void findByUsername_userEntityNotExist_shouldReturnNotPresentOptionalOfUserEntity() {
        Optional<UserEntity> actual = userRepository.findByUsername("1");
        assertFalse(actual.isPresent());
    }

    @Test
    public void existsByUsername_userEntityExist_shouldReturnTrue() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);

        Boolean actual = userRepository.existsByUsername(userEntity.getUsername());

        assertTrue(actual);
    }

    @Test
    public void existsByUsername_userEntityNotExist_shouldReturnFalse() {
        Boolean actual = userRepository.existsByUsername("1");
        assertFalse(actual);
    }

    @Test
    public void existsByEmail_userEntityExist_shouldReturnTrue() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userRepository.save(userEntity);

        Boolean actual = userRepository.existsByEmail(userEntity.getEmail());

        assertTrue(actual);
    }

    @Test
    public void existsByEmail_userEntityNotExist_shouldReturnFalse() {
        Boolean actual = userRepository.existsByEmail("1@gmail.com");
        assertFalse(actual);
    }
}
