package com.javacodeset.service.api;

import com.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import com.javacodeset.dto.executor.JavaCodeExecutionResponseDto;

public interface JavaCodeExecutorService {
    Boolean isExecutorEnabled();
    void enableExecutor();
    void disableExecutor();
    JavaCodeExecutionResponseDto execute(JavaCodeExecutionRequestDto javaCodeExecutionRequestDto);
}
