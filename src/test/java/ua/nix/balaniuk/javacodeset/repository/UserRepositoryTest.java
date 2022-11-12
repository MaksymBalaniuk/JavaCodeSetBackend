package ua.nix.balaniuk.javacodeset.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_userEntityExist_shodReturnPresentOptionalOfUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);

        Optional<UserEntity> actual = userRepository.findByUsername(userEntity.getUsername());

        assertTrue(actual.isPresent());
    }

    @Test
    public void findByUsername_userEntityNotExist_shodReturnNotPresentOptionalOfUserEntity() {
        Optional<UserEntity> actual = userRepository.findByUsername("maxim");
        assertFalse(actual.isPresent());
    }

    @Test
    public void existsByUsername_userEntityExist_shodReturnTrue() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);

        boolean actual = userRepository.existsByUsername(userEntity.getUsername());

        assertTrue(actual);
    }

    @Test
    public void existsByUsername_userEntityNotExist_shodReturnFalse() {
        boolean actual = userRepository.existsByUsername("maxim");
        assertFalse(actual);
    }

    @Test
    public void existsByEmail_userEntityExist_shodReturnTrue() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userRepository.save(userEntity);

        boolean actual = userRepository.existsByEmail(userEntity.getEmail());

        assertTrue(actual);
    }

    @Test
    public void existsByEmail_userEntityNotExist_shodReturnFalse() {
        boolean actual = userRepository.existsByEmail("maxim@gmail.com");
        assertFalse(actual);
    }
}
