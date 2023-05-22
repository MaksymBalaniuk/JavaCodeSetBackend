package com.javacodeset.service;

import com.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import com.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.exception.JavaCodeExecutionException;
import com.javacodeset.service.impl.JavaCodeExecutorServiceImplementation;
import com.javacodeset.tool.JavaCodeExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    public void isExecutorEnabled_shouldReturnBoolean() {
        given(javaCodeExecutor.isEnabled()).willReturn(true);
        Boolean actual = javaCodeExecutorService.isExecutorEnabled();
        assertTrue(actual);
    }

    @Test
    public void enableExecutor_shouldEnableExecutor() {
        javaCodeExecutorService.enableExecutor();
        verify(javaCodeExecutor).enable();
    }

    @Test
    public void disableExecutor_shouldDisableExecutor() {
        javaCodeExecutorService.disableExecutor();
        verify(javaCodeExecutor).disable();
    }

    @Test
    public void execute_validData_shouldReturnAnyJavaCodeExecutionResponseDto() {
        JavaCodeExecutionRequestDto requestDto = DtoExampleStorage.getJavaCodeExecutionRequestDto();
        given(javaCodeExecutor.execute(requestDto.getJavaCode(), requestDto.getArgs(), true))
                .willReturn(new JavaCodeExecutionResponseDto());
        JavaCodeExecutionResponseDto actual = javaCodeExecutorService.execute(requestDto);
        assertNotNull(actual);
    }

    @Test(expected = JavaCodeExecutionException.class)
    public void execute_invalidData_shouldThrowException() {
        JavaCodeExecutionRequestDto requestDto = DtoExampleStorage.getJavaCodeExecutionRequestDto();
        given(javaCodeExecutor.execute(requestDto.getJavaCode(), requestDto.getArgs(), true))
                .willThrow(new JavaCodeExecutionException());
        javaCodeExecutorService.execute(requestDto);
    }
}
