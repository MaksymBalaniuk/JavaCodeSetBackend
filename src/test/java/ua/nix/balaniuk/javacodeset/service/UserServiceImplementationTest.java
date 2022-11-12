package ua.nix.balaniuk.javacodeset.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import ua.nix.balaniuk.javacodeset.dto.UserDto;
import ua.nix.balaniuk.javacodeset.dto.premium.PremiumLimitsDto;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.exception.BadRequestException;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.exception.ProhibitedOperationException;
import ua.nix.balaniuk.javacodeset.repository.AuthorityRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.impl.UserServiceImplementation;
import ua.nix.balaniuk.javacodeset.util.PremiumLimitsPolicy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImplementation userService;

    private static final UUID TEST_USER_UUID = UUID.randomUUID();

    @Test
    public void create_validData_shodReturnSavedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        UserDto userDto = DtoExampleStorage.getUserDto();
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setName("ROLE_USER");

        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(userDto.getEmail())).willReturn(false);
        given(authorityRepository.findByName("ROLE_USER")).willReturn(Optional.of(authorityEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(authorityRepository.save(authorityEntity)).willReturn(authorityEntity);
        given(modelMapper.map(userDto, UserEntity.class)).willReturn(userEntity);

        UserEntity actual = userService.create(userDto);

        assertNotNull(actual);

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyUsername_shodThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto();
        userDto.setUsername("");
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyPassword_shodThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto();
        userDto.setPassword("");
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyEmail_shodThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto();
        userDto.setEmail("");
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_existsByUsername_shodThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto();
        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(true);
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_existsByEmail_shodThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto();
        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(userDto.getEmail())).willReturn(true);
        userService.create(userDto);
    }

    @Test(expected = NotFoundException.class)
    public void create_basicAuthorityNotExist_shodThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto();

        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(userDto.getEmail())).willReturn(false);
        given(authorityRepository.findByName("ROLE_USER")).willReturn(Optional.empty());

        userService.create(userDto);
    }

    @Test
    public void get_validId_shodReturnUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        UserEntity actual = userService.get(TEST_USER_UUID);
        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shodThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.get(TEST_USER_UUID);
    }

    @Test
    public void getAll_shodReturnUserEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        List<UserEntity> expected = List.of(userEntity);

        given(userRepository.findAll()).willReturn(expected);

        List<UserEntity> actual = userService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test(expected = ProhibitedOperationException.class)
    public void update_shodThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto();
        userService.update(userDto);
    }

    @Test(expected = ProhibitedOperationException.class)
    public void delete_shodThrowException() {
        userService.delete(TEST_USER_UUID);
    }

    @Test
    public void getByUsername_userEntityExist_shodReturnUserEntity() {
        UserEntity expected = EntityExampleStorage.getUserEntity();
        given(userRepository.findByUsername(expected.getUsername())).willReturn(Optional.of(expected));
        UserEntity actual = userService.getByUsername(expected.getUsername());
        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getByUsername_userEntityNotExist_shodReturnUserEntity() {
        String username = "maxim";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        userService.getByUsername(username);
    }

    @Test
    public void existsByUsername_shodReturnBoolean() {
        String username = "maxim";
        given(userRepository.existsByUsername(username)).willReturn(true);
        boolean actual = userService.existsByUsername(username);
        assertTrue(actual);
    }

    @Test
    public void existsByEmail_shodReturnBoolean() {
        String email = "maxim@gmail.com";
        given(userRepository.existsByEmail(email)).willReturn(true);
        boolean actual = userService.existsByEmail(email);
        assertTrue(actual);
    }

    @Test
    public void updateUsername_validData_shodReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        String newName = "artur";

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.existsByUsername(newName)).willReturn(false);
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.updateUsername(TEST_USER_UUID, newName);

        assertEquals(newName, actual.getUsername());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void updateUsername_userEntityWithInputIdNotExist_shodThrowException() {
        String newName = "artur";
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.updateUsername(TEST_USER_UUID, newName);
    }

    @Test(expected = BadRequestException.class)
    public void updateUsername_userEntityWithInputUsernameAlreadyExist_shodThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        String newName = "artur";

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.existsByUsername(newName)).willReturn(true);

        userService.updateUsername(TEST_USER_UUID, newName);
    }

    @Test
    public void activateUserById_validData_shodReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setStatus(UserStatus.BANNED);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.activateUserById(TEST_USER_UUID);

        assertEquals(UserStatus.ACTIVE, actual.getStatus());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void activateUserById_userEntityWithInputIdNotExist_shodThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.activateUserById(TEST_USER_UUID);
    }

    @Test(expected = BadRequestException.class)
    public void activateUserById_userEntityHasStatusDeleted_shodThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setStatus(UserStatus.DELETED);
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        userService.activateUserById(TEST_USER_UUID);
    }

    @Test
    public void banUserById_validData_shodReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setStatus(UserStatus.ACTIVE);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.banUserById(TEST_USER_UUID);

        assertEquals(UserStatus.BANNED, actual.getStatus());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void banUserById_userEntityWithInputIdNotExist_shodThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.banUserById(TEST_USER_UUID);
    }

    @Test(expected = BadRequestException.class)
    public void banUserById_userEntityHasStatusDeleted_shodThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setStatus(UserStatus.DELETED);
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        userService.banUserById(TEST_USER_UUID);
    }

    @Test
    public void markUserDeletedById_validData_shodReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setStatus(UserStatus.ACTIVE);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.markUserDeletedById(TEST_USER_UUID);

        assertEquals(UserStatus.DELETED, actual.getStatus());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void markUserDeletedById_userEntityWithInputIdNotExist_shodThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.markUserDeletedById(TEST_USER_UUID);
    }

    @Test
    public void getUserPremiumLimits_validData_shodReturnPremiumLimitsDto() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setPremium(UserPremium.NONE);
        PremiumLimitsDto expected = PremiumLimitsPolicy.getPremiumLimits(UserPremium.NONE);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));

        PremiumLimitsDto actual = userService.getUserPremiumLimits(TEST_USER_UUID);

        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getUserPremiumLimits_userEntityWithInputIdNotExist_shodThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.getUserPremiumLimits(TEST_USER_UUID);
    }
}
