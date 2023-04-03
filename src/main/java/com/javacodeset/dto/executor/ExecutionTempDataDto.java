package com.javacodeset.dto.executor;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.util.List;

@Data
@Builder
public class ExecutionTempDataDto {
    private Path pathToJavaFile;
    private Path pathToDirectory;
    private String mainClassName;
    private List<String> allClassNames;
    private Class<?> mainClass;
}
