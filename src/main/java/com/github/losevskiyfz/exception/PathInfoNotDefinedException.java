package com.github.losevskiyfz.exception;

public class PathInfoNotDefinedException extends RuntimeException {
    public PathInfoNotDefinedException(String message) {
        super(message);
    }

    public PathInfoNotDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathInfoNotDefinedException(Throwable cause) {
        super(cause);
    }
}
