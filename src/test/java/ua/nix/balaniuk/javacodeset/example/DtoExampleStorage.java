package ua.nix.balaniuk.javacodeset.example;

import ua.nix.balaniuk.javacodeset.dto.*;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterRequestDto;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import ua.nix.balaniuk.javacodeset.dto.filter.FilterCodeBlockDto;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;

import java.util.UUID;

public final class DtoExampleStorage {

    public static UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUsername("maxim");
        userDto.setPassword("12345");
        userDto.setEmail("maxim@gmail.com");
        return userDto;
    }

    public static TagDto getTagDto() {
        TagDto tagDto = new TagDto();
        tagDto.setName("#stream");
        return tagDto;
    }

    public static EstimateDto getEstimateDto(UUID userId, UUID codeBlockId) {
        EstimateDto estimateDto = new EstimateDto();
        estimateDto.setUserId(userId);
        estimateDto.setCodeBlockId(codeBlockId);
        estimateDto.setType(EstimateType.LIKE);
        return estimateDto;
    }

    public static CommentDto getCommentDto(UUID userId, UUID codeBlockId) {
        CommentDto commentDto = new CommentDto();
        commentDto.setUserId(userId);
        commentDto.setCodeBlockId(codeBlockId);
        return commentDto;
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

    public static FilterCodeBlockDto getFilterCodeBlockDtoAllFiltersOn() {
        FilterCodeBlockDto filterCodeBlockDto = new FilterCodeBlockDto();
        filterCodeBlockDto.setFilterTitle(true);
        filterCodeBlockDto.setFilterDescription(true);
        filterCodeBlockDto.setFilterContent(true);
        filterCodeBlockDto.setFilterTags(true);
        return filterCodeBlockDto;
    }

    public static AuthenticationRequestDto getAuthenticationRequestDto() {
        AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto();
        authenticationRequestDto.setUsername("maxim");
        authenticationRequestDto.setPassword("12345");
        return authenticationRequestDto;
    }

    public static RegisterRequestDto getRegisterRequestDto() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername("maxim");
        registerRequestDto.setPassword("12345");
        registerRequestDto.setEmail("maxim@gmail.com");
        return registerRequestDto;
    }

    public static JavaCodeExecutionRequestDto getJavaCodeExecutionRequestDto() {
        JavaCodeExecutionRequestDto javaCodeExecutionRequestDto = new JavaCodeExecutionRequestDto();
        javaCodeExecutionRequestDto.setJavaCode("public static void main(String[] args) {}");
        javaCodeExecutionRequestDto.setArgs(new String[] {});
        return javaCodeExecutionRequestDto;
    }
}
