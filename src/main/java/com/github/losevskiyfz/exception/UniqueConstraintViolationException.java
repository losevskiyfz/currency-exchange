package com.github.losevskiyfz.exception;

public class UniqueConstraintViolationException extends RuntimeException{
    public UniqueConstraintViolationException(String message){
        super(message);
    }
    public UniqueConstraintViolationException(String message, Throwable cause){
        super(message, cause);
    }
    public UniqueConstraintViolationException(Throwable cause){
        super(cause);
    }
}
