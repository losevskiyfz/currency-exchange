package com.github.losevskiyfz.exception;

public class ExchangeException extends RuntimeException{
    public ExchangeException(String message) {
        super(message);
    }

    public ExchangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeException(Throwable cause) {
        super(cause);
    }
}
