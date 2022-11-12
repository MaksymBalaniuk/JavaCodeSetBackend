package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionResponseDto;

public interface JavaCodeExecutorService {
    Boolean isExecutorEnabled();
    void enableExecutor();
    void disableExecutor();
    JavaCodeExecutionResponseDto execute(JavaCodeExecutionRequestDto javaCodeExecutionRequestDto);
}
