package com.javacodeset.tool;

import com.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import com.javacodeset.exception.JavaCodeExecutionException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaCodeExecutorTest {

    private final JavaCodeExecutor javaCodeExecutor = new JavaCodeExecutor();

    @Test
    public void isEnabled_shouldReturnBooleanValue() {
        javaCodeExecutor.disable();
        assertFalse(javaCodeExecutor.isEnabled());
        javaCodeExecutor.enable();
        assertTrue(javaCodeExecutor.isEnabled());
    }

    @Test
    public void execute_executorDisabled_shouldThrowException() {
        javaCodeExecutor.disable();
        assertThrows(JavaCodeExecutionException.class, () ->
                javaCodeExecutor.execute("", new String[] {}, true));
        javaCodeExecutor.enable();
    }

    @Test
    public void execute_validCode_shouldReturnJavaCodeExecutionResponseDto() {
        String javaCode = "public static void main(String[] args) { System.out.print(\"Hello world!\"); }";
        JavaCodeExecutionResponseDto expected = new JavaCodeExecutionResponseDto();
        expected.setExitCode(0);
        expected.setOutput("Hello world!");
        expected.setError("");

        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);

        assertEquals(expected, actual);
    }

    @Test
    public void execute_nonCompiledCode_shouldReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        String javaCode = "public static void main(String[] args) { System.out.print(\"Hello world!\") }";
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }

    @Test
    public void execute_codeContainsExitKeyword_shouldReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        String javaCode = "public static void main(String[] args) { System.exit(0); }";
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }

    @Test
    public void execute_codeNotContainsMainMethod_shouldReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute("", new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }

    @Test(expected = JavaCodeExecutionException.class)
    public void execute_codeExecuteTooLong_shouldThrowException() {
        String javaCode = "public static void main(String[] args) { while (true) {} }";
        javaCodeExecutor.execute(javaCode, new String[] {}, true);
    }

    @Test
    public void execute_codeThrowUncheckedException_shouldReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        String javaCode = "public static void main(String[] args) { throw new RuntimeException(); }";
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }
}
