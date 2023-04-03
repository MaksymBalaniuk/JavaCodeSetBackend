package com.javacodeset.dto.executor;

import lombok.Data;

@Data
public class JavaCodeExecutionResponseDto {
    private Integer exitCode;
    private String output;
    private String error;
}
