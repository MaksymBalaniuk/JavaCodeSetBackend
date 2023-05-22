package com.javacodeset.service;

import com.javacodeset.dto.permission.UserPermissionsDto;
import com.javacodeset.entity.AuthorityEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.repository.AuthorityRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.impl.AuthorityServiceImplementation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AuthorityServiceImplementationTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorityServiceImplementation authorityService;

    private static final UUID TEST_USER_UUID = UUID.randomUUID();

    @Test
    public void isUserHasAdminAuthority_validData_shouldReturnTrue() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        AuthorityEntity authorityEntity = EntityExampleStorage.getAuthorityEntity("ROLE_ADMIN");
        userEntity.getAuthorities().add(authorityEntity);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));

        Boolean actual = authorityService.isUserHasAdminAuthority(TEST_USER_UUID);

        assertTrue(actual);
    }

    @Test(expected = NotFoundException.class)
    public void isUserHasAdminAuthority_userEntityNotExist_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        authorityService.isUserHasAdminAuthority(TEST_USER_UUID);
    }

    @Test
    public void isUserHasAdminAuthority_userEntityNotContainsAdminAuthority_shouldReturnFalse() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        Boolean actual = authorityService.isUserHasAdminAuthority(TEST_USER_UUID);
        assertFalse(actual);
    }

    @Test
    public void getUserPermissions_validUserId_noAuthorities_shouldReturnAnyUserPermissionsDtoWithNotNullFields() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        UserPermissionsDto actual = authorityService.getUserPermissions(TEST_USER_UUID);
        assertNotNull(actual);
        assertThat(actual).hasNoNullFieldsOrProperties();
    }

    @Test(expected = NotFoundException.class)
    public void getUserPermissions_invalidUserId_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        authorityService.getUserPermissions(TEST_USER_UUID);
    }

    @Test
    public void addAdminAuthorityToUser_validData_shouldAddAdminAuthorityToUserEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        AuthorityEntity authorityEntity = EntityExampleStorage.getAuthorityEntity("ROLE_ADMIN");
        Set<AuthorityEntity> expectedUserAuthorities = Set.of(authorityEntity);
        Set<UserEntity> expectedAuthorityUsers = Set.of(userEntity);

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(authorityRepository.findByName("ROLE_ADMIN")).willReturn(Optional.of(authorityEntity));
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(authorityRepository.save(authorityEntity)).willReturn(authorityEntity);

        authorityService.addAdminAuthorityToUser(TEST_USER_UUID);
        Set<AuthorityEntity> actualUserAuthorities = userEntity.getAuthorities();
        Set<UserEntity> actualAuthorityUsers = authorityEntity.getUsers();

        assertEquals(expectedUserAuthorities, actualUserAuthorities);
        assertEquals(expectedAuthorityUsers, actualAuthorityUsers);
    }

    @Test(expected = NotFoundException.class)
    public void addAdminAuthorityToUser_userEntityNotExist_shouldThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        authorityService.addAdminAuthorityToUser(TEST_USER_UUID);
    }

    @Test(expected = NotFoundException.class)
    public void addAdminAuthorityToUser_authorityEntityNotExist_shouldThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(authorityRepository.findByName("ROLE_ADMIN")).willReturn(Optional.empty());
        authorityService.addAdminAuthorityToUser(TEST_USER_UUID);
    }
}
