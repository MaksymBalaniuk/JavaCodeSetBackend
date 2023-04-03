package com.javacodeset.exception;

public class ProhibitedOperationException extends RuntimeException {

    public ProhibitedOperationException(String message) {
        super(message);
    }
}
