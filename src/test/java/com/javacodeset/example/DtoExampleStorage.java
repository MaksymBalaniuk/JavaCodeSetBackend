package com.javacodeset.example;

import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.RegisterRequestDto;

public final class DtoExampleStorage {

    public static AuthenticationRequestDto getAuthenticationRequestDto(String username, String password) {
        AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto();
        authenticationRequestDto.setUsername(username);
        authenticationRequestDto.setPassword(password);
        return authenticationRequestDto;
    }

    public static RegisterRequestDto getRegisterRequestDto(String username, String password, String email) {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername(username);
        registerRequestDto.setPassword(password);
        registerRequestDto.setEmail(email);
        return registerRequestDto;
    }
}
