package com.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import com.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import com.javacodeset.service.api.JavaCodeExecutorService;
import com.javacodeset.tool.JavaCodeExecutor;

@Service
@RequiredArgsConstructor
public class JavaCodeExecutorServiceImplementation implements JavaCodeExecutorService {

    private final JavaCodeExecutor javaCodeExecutor;

    @Override
    public Boolean isExecutorEnabled() {
        return javaCodeExecutor.isEnabled();
    }

    @Override
    public void enableExecutor() {
        javaCodeExecutor.enable();
    }

    @Override
    public void disableExecutor() {
        javaCodeExecutor.disable();
    }

    @Override
    public JavaCodeExecutionResponseDto execute(JavaCodeExecutionRequestDto javaCodeExecutionRequestDto) {
        return javaCodeExecutor.execute(javaCodeExecutionRequestDto.getJavaCode(),
                javaCodeExecutionRequestDto.getArgs(), true);
    }
}
