package ua.nix.balaniuk.javacodeset.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.exception.JavaCodeExecutionException;
import ua.nix.balaniuk.javacodeset.service.impl.JavaCodeExecutorServiceImplementation;
import ua.nix.balaniuk.javacodeset.tool.JavaCodeExecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JavaCodeExecutorServiceImplementationTest {

    @Mock
    private JavaCodeExecutor javaCodeExecutor;

    @InjectMocks
    private JavaCodeExecutorServiceImplementation javaCodeExecutorService;

    @Test
    public void isExecutorEnabled_shodReturnBoolean() {
        given(javaCodeExecutor.isEnabled()).willReturn(true);
        boolean actual = javaCodeExecutorService.isExecutorEnabled();
        assertTrue(actual);
    }

    @Test
    public void enableExecutor_shodEnableExecutor() {
        javaCodeExecutorService.enableExecutor();
        verify(javaCodeExecutor).enable();
    }

    @Test
    public void disableExecutor_shodDisableExecutor() {
        javaCodeExecutorService.disableExecutor();
        verify(javaCodeExecutor).disable();
    }

    @Test
    public void execute_validData_shodReturnAnyJavaCodeExecutionResponseDto() {
        JavaCodeExecutionRequestDto requestDto = DtoExampleStorage.getJavaCodeExecutionRequestDto();
        given(javaCodeExecutor.execute(requestDto.getJavaCode(), requestDto.getArgs(), true))
                .willReturn(new JavaCodeExecutionResponseDto());
        JavaCodeExecutionResponseDto actual = javaCodeExecutorService.execute(requestDto);
        assertNotNull(actual);
    }

    @Test(expected = JavaCodeExecutionException.class)
    public void execute_invalidData_shodThrowException() {
        JavaCodeExecutionRequestDto requestDto = DtoExampleStorage.getJavaCodeExecutionRequestDto();
        given(javaCodeExecutor.execute(requestDto.getJavaCode(), requestDto.getArgs(), true))
                .willThrow(new JavaCodeExecutionException());
        javaCodeExecutorService.execute(requestDto);
    }
}
