package ua.nix.balaniuk.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nix.balaniuk.javacodeset.dto.UserDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationResponseDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterResponseDto;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.exception.BadCredentialsException;
import ua.nix.balaniuk.javacodeset.exception.JwtAuthenticationException;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;
import ua.nix.balaniuk.javacodeset.service.api.AuthenticationService;
import ua.nix.balaniuk.javacodeset.service.api.UserService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImplementation implements AuthenticationService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthenticationResponseDto login(AuthenticationRequestDto requestDto) {
        UserEntity user = userService.getByUsername(requestDto.getUsername());

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Invalid username or password");

        if (Objects.equals(user.getStatus(), UserStatus.BANNED))
            throw new JwtAuthenticationException("This account has been banned");

        if (Objects.equals(user.getStatus(), UserStatus.DELETED))
            throw new JwtAuthenticationException("This account has been deleted");

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getAuthorities());
        return new AuthenticationResponseDto(token, user.getId());
    }

    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto requestDto) {
        RegisterResponseDto responseDto = new RegisterResponseDto();
        responseDto.setExistByUsername(userService.existsByUsername(requestDto.getUsername()));
        responseDto.setExistByEmail(userService.existsByEmail(requestDto.getEmail()));

        if (responseDto.getExistByUsername() || responseDto.getExistByEmail())
            return responseDto;

        UserDto userDto = new UserDto();
        userDto.setUsername(requestDto.getUsername());
        userDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userDto.setEmail(requestDto.getEmail());

        UserEntity user = userService.create(userDto);
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getAuthorities());
        responseDto.setToken(token);
        responseDto.setId(user.getId());
        return responseDto;
    }
}
