package com.javacodeset.example;

import com.javacodeset.dto.*;
import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.RegisterRequestDto;
import com.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
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

    public static CommentDto getCommentDto(UUID userId, UUID codeBlockId) {
        CommentDto commentDto = new CommentDto();
        commentDto.setUserId(userId);
        commentDto.setCodeBlockId(codeBlockId);
        return commentDto;
    }

    public static EstimateDto getEstimateDto(UUID userId, UUID codeBlockId) {
        EstimateDto estimateDto = new EstimateDto();
        estimateDto.setUserId(userId);
        estimateDto.setCodeBlockId(codeBlockId);
        return estimateDto;
    }

    public static TagDto getTagDto(String name) {
        TagDto tagDto = new TagDto();
        tagDto.setName(name);
        return tagDto;
    }

    public static UserDto getUserDto(String username, String password, String email) {
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setEmail(email);
        return userDto;
    }

    public static JavaCodeExecutionRequestDto getJavaCodeExecutionRequestDto() {
        JavaCodeExecutionRequestDto javaCodeExecutionRequestDto = new JavaCodeExecutionRequestDto();
        javaCodeExecutionRequestDto.setJavaCode("public static void main(String[] args) {}");
        javaCodeExecutionRequestDto.setArgs(new String[] {});
        return javaCodeExecutionRequestDto;
    }
}
