package com.github.losevskiyfz.exception;

public class InvalidPathInfoException extends RuntimeException{
    public InvalidPathInfoException(String message) {
        super(message);
    }
    public InvalidPathInfoException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidPathInfoException(Throwable cause) {
        super(cause);
    }
}
