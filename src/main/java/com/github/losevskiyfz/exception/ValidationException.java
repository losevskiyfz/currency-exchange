package com.github.losevskiyfz.exception;

public class ValidationException extends RuntimeException {
    public ValidationException (String message) {
        super(message);
    }
    public ValidationException (String message, Throwable cause) {
        super(message, cause);
    }
    public ValidationException (Throwable cause) {
        super(cause);
    }
}
