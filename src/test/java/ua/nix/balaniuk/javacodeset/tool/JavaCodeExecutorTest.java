package ua.nix.balaniuk.javacodeset.tool;

import org.junit.Test;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import ua.nix.balaniuk.javacodeset.exception.JavaCodeExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JavaCodeExecutorTest {

    private final JavaCodeExecutor javaCodeExecutor = new JavaCodeExecutor();

    @Test
    public void isEnabled_shodReturnBooleanValue() {
        javaCodeExecutor.disable();
        assertFalse(javaCodeExecutor.isEnabled());
        javaCodeExecutor.enable();
        assertTrue(javaCodeExecutor.isEnabled());
    }

    @Test
    public void execute_executorDisabled_shodThrowException() {
        javaCodeExecutor.disable();
        assertThrows(JavaCodeExecutionException.class, () ->
                javaCodeExecutor.execute("", new String[] {}, true));
        javaCodeExecutor.enable();
    }

    @Test
    public void execute_validCode_shodReturnJavaCodeExecutionResponseDto() {
        String javaCode = "public static void main(String[] args) { System.out.print(\"Hello world!\"); }";
        JavaCodeExecutionResponseDto expected = new JavaCodeExecutionResponseDto();
        expected.setExitCode(0);
        expected.setOutput("Hello world!");
        expected.setError("");

        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);

        assertEquals(expected, actual);
    }

    @Test
    public void execute_nonCompiledCode_shodReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        String javaCode = "public static void main(String[] args) { System.out.print(\"Hello world!\") }";
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }

    @Test
    public void execute_codeContainsExitKeyword_shodReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        String javaCode = "public static void main(String[] args) { System.exit(0); }";
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }

    @Test
    public void execute_codeNotContainsMainMethod_shodReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute("", new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }

    @Test(expected = JavaCodeExecutionException.class)
    public void execute_codeExecuteTooLong_shodThrowException() {
        String javaCode = "public static void main(String[] args) { while (true) {} }";
        javaCodeExecutor.execute(javaCode, new String[] {}, true);
    }

    @Test
    public void execute_codeThrowUncheckedException_shodReturnJavaCodeExecutionResponseDtoWithExitCode1() {
        String javaCode = "public static void main(String[] args) { throw new RuntimeException(); }";
        JavaCodeExecutionResponseDto actual = javaCodeExecutor.execute(javaCode, new String[] {}, true);
        assertThat(actual).hasFieldOrPropertyWithValue("exitCode", 1);
    }
}
