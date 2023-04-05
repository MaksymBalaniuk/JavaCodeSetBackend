package com.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.UserDto;
import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.AuthenticationResponseDto;
import com.javacodeset.dto.auth.RegisterRequestDto;
import com.javacodeset.dto.auth.RegisterResponseDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.exception.BadCredentialsException;
import com.javacodeset.exception.JwtAuthenticationException;
import com.javacodeset.security.jwt.JwtProvider;
import com.javacodeset.service.api.AuthenticationService;
import com.javacodeset.service.api.UserService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImplementation implements AuthenticationService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthenticationResponseDto login(AuthenticationRequestDto requestDto) {
        UserEntity user = userService.getUserByUsername(requestDto.getUsername());

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Invalid username or password");

        if (Objects.equals(user.getStatus(), UserStatus.BANNED))
            throw new JwtAuthenticationException("This account has been banned");

        if (Objects.equals(user.getStatus(), UserStatus.DELETED))
            throw new JwtAuthenticationException("This account has been deleted");

        String token = jwtProvider.createToken(user.getUsername(), user.getAuthorities());
        return new AuthenticationResponseDto(token, user.getId());
    }

    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto requestDto) {
        RegisterResponseDto responseDto = new RegisterResponseDto();
        responseDto.setExistsByUsername(userService.existsUserByUsername(requestDto.getUsername()));
        responseDto.setExistsByEmail(userService.existsUserByEmail(requestDto.getEmail()));

        if (responseDto.getExistsByUsername() || responseDto.getExistsByEmail())
            return responseDto;

        UserDto userDto = new UserDto();
        userDto.setUsername(requestDto.getUsername());
        userDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userDto.setEmail(requestDto.getEmail());

        UserEntity user = userService.create(userDto);
        String token = jwtProvider.createToken(user.getUsername(), user.getAuthorities());
        responseDto.setToken(token);
        responseDto.setId(user.getId());
        return responseDto;
    }
}
