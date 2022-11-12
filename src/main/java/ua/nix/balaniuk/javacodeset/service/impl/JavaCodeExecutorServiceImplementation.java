package ua.nix.balaniuk.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import ua.nix.balaniuk.javacodeset.service.api.JavaCodeExecutorService;
import ua.nix.balaniuk.javacodeset.tool.JavaCodeExecutor;

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
