package com.javacodeset.dto.executor;

import lombok.Data;

@Data
public class JavaCodeExecutionRequestDto {
    private String javaCode;
    private String[] args;
}
