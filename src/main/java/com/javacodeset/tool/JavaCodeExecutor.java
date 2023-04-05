package com.javacodeset.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.javacodeset.dto.executor.ExecutionTempDataDto;
import com.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import com.javacodeset.exception.InternalExecutorException;
import com.javacodeset.exception.JavaCodeExecutionException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Component
public class JavaCodeExecutor {

    private final AtomicBoolean isEnabled = new AtomicBoolean(true);
    private final Object MONITOR = new Object();
    private static final Integer CODE_EXECUTION_TIMEOUT_IN_SECONDS = 10;
    private static final String FILE_PREFIX = "temp";
    private static final String DIRECTORY_PREFIX = "dir";
    private static final String JAVA_FILE_SUFFIX = ".java";
    private static final String CLASS_FILE_SUFFIX = ".class";
    private static final String SEPARATOR = "\\.";
    private static final String SPACE = " ";
    private static final String QUOTE = "\"";
    private static final String MAIN_CLASS_TEMPLATE = "public class";
    private static final String STATIC_MAIN_METHOD_TEMPLATE = "public static void main(String[] args)";
    private static final String OPEN_BRACKET = "{";
    private static final String CLOSE_BRACKET = "}";
    private static final String EMPTY_STRING = "";
    private static final String REPLACED_CLASS_NAME = "Main";
    private static final String REPLACED_JAVA_FILE_NAME = REPLACED_CLASS_NAME + JAVA_FILE_SUFFIX;
    private static final String EXIT = "exit";
    private static final String EXECUTOR_DISABLED_ERROR_MESSAGE = "Executor is disabled";
    private static final String JAVA_FILE_CREATION_ERROR_MESSAGE = "Unable to create temporary java file";
    private static final String JAVA_FILE_TEMPLATE_ERROR_MESSAGE = "Error in file template";
    private static final String CLASS_LOAD_ERROR_MESSAGE = "Error loading class";
    private static final String MAIN_METHOD_RUN_ERROR_MESSAGE = "Error calling main method";
    private static final String DELETE_FILES_ERROR_MESSAGE = "Error while deleting files";
    private static final String READ_FILES_ERROR_MESSAGE = "Error while reading files";
    private static final String RESERVED_WORD_MESSAGE = " is a reserved word, exclude it from your code";
    private static final String TIMEOUT_ERROR_MESSAGE = "Code execution time out";
    private static final String EXECUTING_UNKNOWN_ERROR_MESSAGE = "Unknown error while executing code";
    private static final String EXIT_USE_ERROR_MESSAGE = QUOTE + EXIT + QUOTE + RESERVED_WORD_MESSAGE;
    private static final String MAIN_METHOD_NOT_EXIST_ERROR_MESSAGE =
            "Class must contain the main method with the signature: " + QUOTE + STATIC_MAIN_METHOD_TEMPLATE + QUOTE;
    private static final String IMPORTS =
            """
            import java.lang.*;
            import java.util.*;
            import java.text.*;
            import java.io.*;
            import java.nio.*;
            import java.time.*;
            import java.math.*;
            """;

    public boolean isEnabled() {
        return isEnabled.get();
    }

    public void enable() {
        isEnabled.set(true);
    }

    public void disable() {
        isEnabled.set(false);
    }

    public JavaCodeExecutionResponseDto execute(String javaCode, String[] args, boolean safeMode) {
        if (!isEnabled())
            throw new JavaCodeExecutionException(EXECUTOR_DISABLED_ERROR_MESSAGE);

        JavaCodeExecutionResponseDto response = new JavaCodeExecutionResponseDto();

        if (!inspectCode(response, javaCode, safeMode))
            return response;

        ExecutionTempDataDto tempData = createTempJavaFile();
        createClassTemplate(tempData, javaCode);

        if(!compileJavaFile(tempData, response))
            return response;

        readAllClasses(tempData);
        loadClasses(tempData);
        runCode(tempData, response, args);
        deleteTempDirectory(tempData.getPathToDirectory());
        return response;
    }

    private boolean inspectCode(JavaCodeExecutionResponseDto response, String javaCode, boolean safeMode) {
        if (safeMode && javaCode.contains(EXIT))
            response.setError(EXIT_USE_ERROR_MESSAGE);

        if (!javaCode.contains(STATIC_MAIN_METHOD_TEMPLATE))
            response.setError(MAIN_METHOD_NOT_EXIST_ERROR_MESSAGE);

        if (Objects.nonNull(response.getError())) {
            response.setExitCode(1);
            response.setOutput(EMPTY_STRING);
            return false;
        }
        return true;
    }

    private ExecutionTempDataDto createTempJavaFile() {
        Path pathToDirectory = null;
        Path pathToJavaFile;
        String mainClassName;

        try {
            pathToDirectory = Files.createTempDirectory(DIRECTORY_PREFIX);
            pathToJavaFile = Files.createTempFile(pathToDirectory, FILE_PREFIX, JAVA_FILE_SUFFIX);
            mainClassName = pathToJavaFile.getFileName().toString().split(SEPARATOR)[0];
        } catch (Exception ex) {
            if (Objects.nonNull(pathToDirectory))
                deleteTempDirectory(pathToDirectory);
            throw new InternalExecutorException(JAVA_FILE_CREATION_ERROR_MESSAGE);
        }

        return ExecutionTempDataDto.builder()
                .pathToJavaFile(pathToJavaFile)
                .pathToDirectory(pathToDirectory)
                .mainClassName(mainClassName)
                .build();
    }

    private void createClassTemplate(ExecutionTempDataDto tempData, String javaCode) {
        String classTemplate = buildClassTemplate(tempData.getMainClassName(), javaCode);
        try {
            Files.writeString(tempData.getPathToJavaFile(), classTemplate);
        } catch (Exception ex) {
            deleteTempDirectory(tempData.getPathToDirectory());
            throw new InternalExecutorException(JAVA_FILE_TEMPLATE_ERROR_MESSAGE);
        }
    }

    private String buildClassTemplate(String mainClassName, String javaCode) {
        return IMPORTS + System.lineSeparator() +
                MAIN_CLASS_TEMPLATE + SPACE + mainClassName + SPACE + OPEN_BRACKET +
                System.lineSeparator() + javaCode + System.lineSeparator() + CLOSE_BRACKET;
    }

    private boolean compileJavaFile(ExecutionTempDataDto tempData, JavaCodeExecutionResponseDto response) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        ByteArrayOutputStream compileErrorStream = new ByteArrayOutputStream();

        int exitCode = compiler.run(null, null, compileErrorStream,
                String.valueOf(tempData.getPathToJavaFile()));
        response.setExitCode(exitCode);

        if (compileErrorStream.toString().isEmpty())
            return true;
        else {
            response.setError(replaceMainClassName(tempData, compileErrorStream.toString()));
            response.setOutput(EMPTY_STRING);
            deleteTempDirectory(tempData.getPathToDirectory());
            return false;
        }
    }

    private String replaceMainClassName(ExecutionTempDataDto tempData, String source) {
        return source
                .replace(tempData.getPathToJavaFile().toString(), REPLACED_JAVA_FILE_NAME)
                .replace(tempData.getMainClassName(), REPLACED_CLASS_NAME);
    }

    private void readAllClasses(ExecutionTempDataDto tempData) {
        try (Stream<Path> pathStream = Files.walk(tempData.getPathToDirectory())) {
            List<String> classNames = pathStream
                    .map(Path::getFileName)
                    .map(String::valueOf)
                    .filter(f -> f.endsWith(CLASS_FILE_SUFFIX))
                    .map(f -> f.split(SEPARATOR)[0])
                    .toList();
            tempData.setAllClassNames(new ArrayList<>(classNames));
        } catch (Exception ex) {
            throw new InternalExecutorException(READ_FILES_ERROR_MESSAGE);
        }
    }

    private void loadClasses(ExecutionTempDataDto tempData) {
        try (URLClassLoader classLoader = new URLClassLoader(
                new URL[] { new File(tempData.getPathToDirectory().toString()).toURI().toURL() })) {
            tempData.setMainClass(classLoader.loadClass(tempData.getMainClassName()));
            tempData.getAllClassNames().remove(tempData.getMainClassName());
            for (String className : tempData.getAllClassNames())
                classLoader.loadClass(className);
        } catch (Exception ex) {
            throw new InternalExecutorException(CLASS_LOAD_ERROR_MESSAGE);
        }
    }

    private void runCode(ExecutionTempDataDto tempData, JavaCodeExecutionResponseDto response, String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(new RunCodeTask(tempData, response, args));
        try {
            future.get(CODE_EXECUTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new JavaCodeExecutionException(TIMEOUT_ERROR_MESSAGE);
        } catch (Exception ex) {
            throw new InternalExecutorException(EXECUTING_UNKNOWN_ERROR_MESSAGE);
        } finally {
            executor.shutdownNow();
        }
    }

    private void deleteTempDirectory(Path pathToDirectory) {
        try (Stream<Path> pathStream = Files.walk(pathToDirectory)) {
            List<Path> paths = pathStream.sorted(Comparator.reverseOrder()).toList();
            for (Path path : paths)
                Files.delete(path);
        } catch (Exception ex) {
            throw new InternalExecutorException(DELETE_FILES_ERROR_MESSAGE);
        }
    }

    @RequiredArgsConstructor
    private class RunCodeTask implements Runnable {

        private final ExecutionTempDataDto tempData;
        private final JavaCodeExecutionResponseDto response;
        private final String[] args;

        @Override
        public void run() {
            Method mainMethod;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

            try {
                mainMethod = tempData.getMainClass().getMethod("main", String[].class);
            } catch (Exception ex) {
                throw new InternalExecutorException(MAIN_METHOD_RUN_ERROR_MESSAGE);
            }

            synchronized (MONITOR) {
                PrintStream systemOut = System.out;
                PrintStream systemErr = System.err;
                System.setOut(new PrintStream(outputStream));
                System.setErr(new PrintStream(errorStream));
                try {
                    mainMethod.invoke(null, (Object) args);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setExitCode(1);
                }
                System.setOut(systemOut);
                System.setErr(systemErr);
            }

            response.setOutput(replaceMainClassName(tempData, outputStream.toString()));
            response.setError(replaceMainClassName(tempData, errorStream.toString()));
        }
    }
}
