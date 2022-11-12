package ua.nix.balaniuk.javacodeset.dto.executor;

import lombok.Data;

@Data
public class JavaCodeExecutionRequestDto {
    private String javaCode;
    private String[] args;
}
