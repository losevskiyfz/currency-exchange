package com.github.losevskiyfz.exception;

public class SqlObjectNotFoundException extends RuntimeException{
    public SqlObjectNotFoundException(String message) {
        super(message);
    }
    public SqlObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public SqlObjectNotFoundException(Throwable cause) {
        super(cause);
    }
}
