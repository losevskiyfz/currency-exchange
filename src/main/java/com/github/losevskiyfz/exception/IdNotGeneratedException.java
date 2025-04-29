package com.github.losevskiyfz.exception;

public class IdNotGeneratedException extends RuntimeException{
    public IdNotGeneratedException(String message) {
        super(message);
    }

    public IdNotGeneratedException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdNotGeneratedException(Throwable cause) {
        super(cause);
    }
}
