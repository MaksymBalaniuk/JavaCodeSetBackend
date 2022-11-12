package ua.nix.balaniuk.javacodeset.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nix.balaniuk.javacodeset.dto.permission.UserPermissionsDto;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.repository.AuthorityRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.impl.AuthorityServiceImplementation;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void getByName_authorityEntityExist_shodReturnAuthorityEntity() {
        AuthorityEntity expected = EntityExampleStorage.getAuthorityEntity();
        given(authorityRepository.findByName(expected.getName())).willReturn(Optional.of(expected));
        AuthorityEntity actual = authorityService.getByName(expected.getName());
        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getByName_authorityEntityNotExist_shodThrowException() {
        String authorityName = "ANOTHER_AUTHORITY";
        given(authorityRepository.findByName(authorityName)).willReturn(Optional.empty());
        authorityService.getByName(authorityName);
    }

    @Test
    public void getUserPermissions_validUserId_noAuthorities_shodReturnAnyUserPermissionsDtoWithNotNullFields() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        UserPermissionsDto actual = authorityService.getUserPermissions(TEST_USER_UUID);
        assertNotNull(actual);
        assertThat(actual).hasNoNullFieldsOrProperties();
    }

    @Test(expected = NotFoundException.class)
    public void getUserPermissions_invalidUserId_shodThrowException() {
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        authorityService.getUserPermissions(TEST_USER_UUID);
    }
}
