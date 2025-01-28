package com.aurora.core.exception;

public class AuroraException extends RuntimeException {
    public AuroraException(String message) {
        super(message);
    }

    public AuroraException(String message, Throwable cause) {
        super(message, cause);
    }
}
