package ua.nix.balaniuk.javacodeset.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.nix.balaniuk.javacodeset.dto.UserDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationResponseDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterResponseDto;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.exception.BadCredentialsException;
import ua.nix.balaniuk.javacodeset.exception.JwtAuthenticationException;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;
import ua.nix.balaniuk.javacodeset.service.api.UserService;
import ua.nix.balaniuk.javacodeset.service.impl.AuthenticationServiceImplementation;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceImplementationTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImplementation authenticationService;

    private static final UUID TEST_USER_UUID = UUID.randomUUID();

    @Test
    public void login_validData_shodReturnAuthenticationResponseDto() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto();
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setId(TEST_USER_UUID);
        userEntity.setStatus(UserStatus.ACTIVE);
        String token = "token";
        AuthenticationResponseDto expected = new AuthenticationResponseDto(token, userEntity.getId());

        given(userService.getByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(true);
        given(jwtTokenProvider.createToken(userEntity.getUsername(), userEntity.getAuthorities()))
                .willReturn(token);

        AuthenticationResponseDto actual = authenticationService.login(requestDto);

        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void login_userEntityNotExist_shodThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto();
        given(userService.getByUsername(requestDto.getUsername())).willThrow(NotFoundException.class);
        authenticationService.login(requestDto);
    }

    @Test(expected = BadCredentialsException.class)
    public void login_invalidPassword_shodThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto();
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setId(TEST_USER_UUID);

        given(userService.getByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(false);

        authenticationService.login(requestDto);
    }

    @Test(expected = JwtAuthenticationException.class)
    public void login_userBanned_shodThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto();
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setId(TEST_USER_UUID);
        userEntity.setStatus(UserStatus.BANNED);

        given(userService.getByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(true);

        authenticationService.login(requestDto);
    }

    @Test(expected = JwtAuthenticationException.class)
    public void login_userDeleted_shodThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto();
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setId(TEST_USER_UUID);
        userEntity.setStatus(UserStatus.DELETED);

        given(userService.getByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(true);

        authenticationService.login(requestDto);
    }

    @Test
    public void register_validData_shodReturnRegisterResponseDto() {
        RegisterRequestDto requestDto = DtoExampleStorage.getRegisterRequestDto();
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        userEntity.setId(TEST_USER_UUID);
        String token = "token";
        RegisterResponseDto expected = new RegisterResponseDto(
                false, false, token, userEntity.getId());

        given(userService.existsByUsername(requestDto.getUsername())).willReturn(false);
        given(userService.existsByEmail(requestDto.getEmail())).willReturn(false);
        given(passwordEncoder.encode(requestDto.getPassword())).willReturn(requestDto.getPassword());
        given(userService.create(any(UserDto.class))).willReturn(userEntity);
        given(jwtTokenProvider.createToken(userEntity.getUsername(), userEntity.getAuthorities())).willReturn(token);

        RegisterResponseDto actual = authenticationService.register(requestDto);

        assertEquals(expected, actual);

        verify(userService).create(any(UserDto.class));
    }

    @Test
    public void register_usernameOrEmailExist_shodReturnRegisterResponseDto() {
        RegisterRequestDto requestDto = DtoExampleStorage.getRegisterRequestDto();
        RegisterResponseDto expected = new RegisterResponseDto(
                true, true, null, null);

        given(userService.existsByUsername(requestDto.getUsername())).willReturn(true);
        given(userService.existsByEmail(requestDto.getEmail())).willReturn(true);

        RegisterResponseDto actual = authenticationService.register(requestDto);

        assertEquals(expected, actual);
    }
}
