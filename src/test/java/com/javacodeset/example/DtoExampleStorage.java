package com.javacodeset.example;

import com.javacodeset.dto.CodeBlockDto;
import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.RegisterRequestDto;
import com.javacodeset.dto.filter.FilterCodeBlockDto;

import java.util.UUID;

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

    public static CodeBlockDto getCodeBlockDto(UUID userId) {
        CodeBlockDto codeBlockDto = new CodeBlockDto();
        codeBlockDto.setUserId(userId);
        return codeBlockDto;
    }

    public static FilterCodeBlockDto getFilterCodeBlockDtoAllFiltersOff() {
        FilterCodeBlockDto filterCodeBlockDto = new FilterCodeBlockDto();
        filterCodeBlockDto.setFilterTitle(false);
        filterCodeBlockDto.setFilterDescription(false);
        filterCodeBlockDto.setFilterContent(false);
        filterCodeBlockDto.setFilterTags(false);
        return filterCodeBlockDto;
    }
}
