package com.javacodeset.service;

import com.javacodeset.dto.UserDto;
import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.AuthenticationResponseDto;
import com.javacodeset.dto.auth.RegisterRequestDto;
import com.javacodeset.dto.auth.RegisterResponseDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.exception.BadCredentialsException;
import com.javacodeset.exception.JwtAuthenticationException;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.security.jwt.JwtProvider;
import com.javacodeset.service.api.UserService;
import com.javacodeset.service.impl.AuthenticationServiceImplementation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private JwtProvider jwtProvider;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImplementation authenticationService;

    private static final UUID TEST_USER_UUID = UUID.randomUUID();

    @Test
    public void login_validData_shouldReturnAuthenticationResponseDto() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto("1", "1");
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setId(TEST_USER_UUID);
        userEntity.setStatus(UserStatus.ACTIVE);
        String token = "token";
        AuthenticationResponseDto expected = new AuthenticationResponseDto(token, userEntity.getId());

        given(userService.getUserByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(true);
        given(jwtProvider.createToken(userEntity.getUsername(), userEntity.getAuthorities())).willReturn(token);

        AuthenticationResponseDto actual = authenticationService.login(requestDto);

        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void login_userEntityNotExist_shouldThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto("1", "1");
        given(userService.getUserByUsername(requestDto.getUsername())).willThrow(NotFoundException.class);
        authenticationService.login(requestDto);
    }

    @Test(expected = BadCredentialsException.class)
    public void login_invalidPassword_shouldThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto("1", "1");
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setId(TEST_USER_UUID);

        given(userService.getUserByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(false);

        authenticationService.login(requestDto);
    }

    @Test(expected = JwtAuthenticationException.class)
    public void login_userBanned_shouldThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto("1", "1");
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setId(TEST_USER_UUID);
        userEntity.setStatus(UserStatus.BANNED);

        given(userService.getUserByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(true);

        authenticationService.login(requestDto);
    }

    @Test(expected = JwtAuthenticationException.class)
    public void login_userDeleted_shouldThrowException() {
        AuthenticationRequestDto requestDto = DtoExampleStorage.getAuthenticationRequestDto("1", "1");
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setId(TEST_USER_UUID);
        userEntity.setStatus(UserStatus.DELETED);

        given(userService.getUserByUsername(requestDto.getUsername())).willReturn(userEntity);
        given(passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword())).willReturn(true);

        authenticationService.login(requestDto);
    }

    @Test
    public void register_validData_shouldReturnRegisterResponseDto() {
        RegisterRequestDto requestDto = DtoExampleStorage.getRegisterRequestDto("1", "1", "1@gmail.com");
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setId(TEST_USER_UUID);
        String token = "token";
        RegisterResponseDto expected = new RegisterResponseDto(false, false, token, userEntity.getId());

        given(userService.existsUserByUsername(requestDto.getUsername())).willReturn(false);
        given(userService.existsUserByEmail(requestDto.getEmail())).willReturn(false);
        given(passwordEncoder.encode(requestDto.getPassword())).willReturn(requestDto.getPassword());
        given(userService.create(any(UserDto.class))).willReturn(userEntity);
        given(jwtProvider.createToken(userEntity.getUsername(), userEntity.getAuthorities())).willReturn(token);

        RegisterResponseDto actual = authenticationService.register(requestDto);

        assertEquals(expected, actual);

        verify(userService).create(any(UserDto.class));
    }

    @Test
    public void register_usernameOrEmailExist_shouldReturnRegisterResponseDto() {
        RegisterRequestDto requestDto = DtoExampleStorage.getRegisterRequestDto("1", "1", "1@gmail.com");
        RegisterResponseDto expected = new RegisterResponseDto(true, true, null, null);

        given(userService.existsUserByUsername(requestDto.getUsername())).willReturn(true);
        given(userService.existsUserByEmail(requestDto.getEmail())).willReturn(true);

        RegisterResponseDto actual = authenticationService.register(requestDto);

        assertEquals(expected, actual);
    }
}
