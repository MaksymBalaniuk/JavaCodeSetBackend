package com.javacodeset.service;

import com.javacodeset.dto.UserDto;
import com.javacodeset.dto.premium.PremiumLimitsDto;
import com.javacodeset.entity.AuthorityEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserPremium;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.exception.BadRequestException;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.exception.ProhibitedOperationException;
import com.javacodeset.repository.AuthorityRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.impl.UserServiceImplementation;
import com.javacodeset.util.PremiumLimitsPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

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
    public void create_validData_shouldReturnSavedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        UserDto userDto = DtoExampleStorage.getUserDto("1", "1", "1@gmail.com");
        AuthorityEntity authorityEntity = EntityExampleStorage.getAuthorityEntity("ROLE_USER");

        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(userDto.getEmail())).willReturn(false);
        given(authorityRepository.findByName(authorityEntity.getName())).willReturn(Optional.of(authorityEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(authorityRepository.save(authorityEntity)).willReturn(authorityEntity);
        given(modelMapper.map(userDto, UserEntity.class)).willReturn(userEntity);

        UserEntity actual = userService.create(userDto);

        assertNotNull(actual);

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyUsername_shouldThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto("", "1", "1@gmail.com");
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyPassword_shouldThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto("1", "", "1@gmail.com");
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyEmail_shouldThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto("1", "1", "");
        userDto.setEmail("");
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_existsByUsername_shouldThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto("1", "1", "1@gmail.com");
        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(true);
        userService.create(userDto);
    }

    @Test(expected = BadRequestException.class)
    public void create_existsByEmail_shouldThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto("1", "1", "1@gmail.com");
        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(userDto.getEmail())).willReturn(true);
        userService.create(userDto);
    }

    @Test(expected = NotFoundException.class)
    public void create_basicAuthorityNotExist_shouldThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto("1", "1", "1@gmail.com");

        given(userRepository.existsByUsername(userDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(userDto.getEmail())).willReturn(false);
        given(authorityRepository.findByName("ROLE_USER")).willReturn(Optional.empty());

        userService.create(userDto);
    }

    @Test
    public void get_validId_shouldReturnUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        UserEntity actual = userService.get(TEST_USER_UUID);
        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.get(TEST_USER_UUID);
    }

    @Test
    public void getAll_shouldReturnUserEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        List<UserEntity> expected = List.of(userEntity);

        given(userRepository.findAll()).willReturn(expected);

        List<UserEntity> actual = userService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test(expected = ProhibitedOperationException.class)
    public void update_shouldThrowException() {
        UserDto userDto = DtoExampleStorage.getUserDto("1", "1", "1@gmail.com");
        userService.update(userDto);
    }

    @Test(expected = ProhibitedOperationException.class)
    public void delete_shouldThrowException() {
        userService.delete(TEST_USER_UUID);
    }

    @Test
    public void getUserByUsername_userEntityExist_shouldReturnUserEntity() {
        UserEntity expected = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        given(userRepository.findByUsername(expected.getUsername())).willReturn(Optional.of(expected));
        UserEntity actual = userService.getUserByUsername(expected.getUsername());
        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getByUsername_userEntityNotExist_shouldThrowException() {
        String username = "1";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        userService.getUserByUsername(username);
    }

    @Test
    public void searchUsersByUsername_userEntityExist_shouldReturnNotEmptyUserEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        List<UserEntity> expected = List.of(userEntity);

        given(userRepository.findAll()).willReturn(expected);

        List<UserEntity> actual = userService.searchUsersByUsername(userEntity.getUsername());

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void searchUsersByUsername_userEntityNotExist_shouldReturnEmptyUserEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        given(userRepository.findAll()).willReturn(List.of(userEntity));
        List<UserEntity> actual = userService.searchUsersByUsername("2");
        assertTrue(actual.isEmpty());
    }

    @Test
    public void existsUserByUsername_shouldReturnBoolean() {
        String username = "1";
        given(userRepository.existsByUsername(username)).willReturn(true);
        Boolean actual = userService.existsUserByUsername(username);
        assertTrue(actual);
    }

    @Test
    public void existsUserByEmail_shouldReturnBoolean() {
        String email = "1@gmail.com";
        given(userRepository.existsByEmail(email)).willReturn(true);
        Boolean actual = userService.existsUserByEmail(email);
        assertTrue(actual);
    }

    @Test
    public void updateUserPremium_validData_shouldReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setPremium(UserPremium.NONE);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.updateUserPremium(TEST_USER_UUID, UserPremium.ORDINARY);

        assertEquals(UserPremium.ORDINARY, actual.getPremium());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void updateUserPremium_userEntityNotExist_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.updateUserPremium(TEST_USER_UUID, UserPremium.ORDINARY);
    }

    @Test
    public void activateUserById_validData_shouldReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setStatus(UserStatus.BANNED);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.activateUserById(TEST_USER_UUID);

        assertEquals(UserStatus.ACTIVE, actual.getStatus());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void activateUserById_userEntityNotExist_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.activateUserById(TEST_USER_UUID);
    }

    @Test(expected = BadRequestException.class)
    public void activateUserById_userEntityHasStatusDeleted_shouldThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setStatus(UserStatus.DELETED);
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        userService.activateUserById(TEST_USER_UUID);
    }

    @Test
    public void banUserById_validData_shouldReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setStatus(UserStatus.ACTIVE);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.banUserById(TEST_USER_UUID);

        assertEquals(UserStatus.BANNED, actual.getStatus());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void banUserById_userEntityNotExist_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.banUserById(TEST_USER_UUID);
    }

    @Test(expected = BadRequestException.class)
    public void banUserById_userEntityHasStatusDeleted_shouldThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setStatus(UserStatus.DELETED);
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        userService.banUserById(TEST_USER_UUID);
    }

    @Test
    public void markUserDeletedById_validData_shouldReturnUpdatedUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setStatus(UserStatus.ACTIVE);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);

        UserEntity actual = userService.markUserDeletedById(TEST_USER_UUID);

        assertEquals(UserStatus.DELETED, actual.getStatus());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void markUserDeletedById_userEntityNotExist_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.markUserDeletedById(TEST_USER_UUID);
    }

    @Test
    public void getUserPremiumLimits_validData_shouldReturnPremiumLimitsDto() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setPremium(UserPremium.NONE);
        PremiumLimitsDto expected = PremiumLimitsPolicy.getPremiumLimits(UserPremium.NONE);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));

        PremiumLimitsDto actual = userService.getUserPremiumLimits(TEST_USER_UUID);

        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getUserPremiumLimits_userEntityNotExist_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        userService.getUserPremiumLimits(TEST_USER_UUID);
    }
}
