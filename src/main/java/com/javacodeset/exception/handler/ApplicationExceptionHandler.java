package com.javacodeset.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.javacodeset.dto.error.ErrorResponseDto;
import com.javacodeset.exception.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(
            NotFoundException notFoundException) {
        log.warn(notFoundException.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(HttpServletResponse.SC_NOT_FOUND,
                notFoundException.getMessage(), Instant.now()), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException badRequestException) {
        log.warn(badRequestException.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(HttpServletResponse.SC_BAD_REQUEST,
                badRequestException.getMessage(), Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProhibitedOperationException.class)
    public ResponseEntity<Object> handleProhibitedOperationException(
            ProhibitedOperationException prohibitedOperationException) {
        log.warn(prohibitedOperationException.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(HttpServletResponse.SC_BAD_REQUEST,
                prohibitedOperationException.getMessage(), Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JavaCodeExecutionException.class)
    public ResponseEntity<Object> handleJavaCodeExecutionException(
            JavaCodeExecutionException javaCodeExecutionException) {
        log.warn(javaCodeExecutionException.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(HttpServletResponse.SC_BAD_REQUEST,
                javaCodeExecutionException.getMessage(), Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalExecutorException.class)
    public ResponseEntity<Object> handleInternalExecutorException(
            InternalExecutorException internalExecutorException) {
        log.error(internalExecutorException.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                internalExecutorException.getMessage(), Instant.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadCredentialsException badCredentialsException) {
        log.warn(badCredentialsException.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(HttpServletResponse.SC_UNAUTHORIZED,
                badCredentialsException.getMessage(), Instant.now()), HttpStatus.UNAUTHORIZED);
    }
}
