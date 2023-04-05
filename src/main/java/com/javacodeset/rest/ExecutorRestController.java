package com.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import com.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import com.javacodeset.service.api.JavaCodeExecutorService;

@RestController
@RequestMapping("/api/executor")
@RequiredArgsConstructor
public class ExecutorRestController {

    private final JavaCodeExecutorService javaCodeExecutorService;

    @GetMapping("/is-enabled")
    public ResponseEntity<Boolean> isExecutorEnabled() {
        return new ResponseEntity<>(javaCodeExecutorService.isExecutorEnabled(), HttpStatus.OK);
    }

    @PostMapping("/enable")
    public ResponseEntity<Object> enableExecutor() {
        javaCodeExecutorService.enableExecutor();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/disable")
    public ResponseEntity<Object> disableExecutor() {
        javaCodeExecutorService.disableExecutor();
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/execute")
    public JavaCodeExecutionResponseDto execute(
            @RequestBody JavaCodeExecutionRequestDto javaCodeExecutionRequestDto) {
        return javaCodeExecutorService.execute(javaCodeExecutionRequestDto);
    }
}
