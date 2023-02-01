package com.paulgougassian.service.ex;

public class IllegalUserTypeException extends RuntimeException {
    public IllegalUserTypeException() {
    }

    public IllegalUserTypeException(String message) {
        super(message);
    }

    public IllegalUserTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalUserTypeException(Throwable cause) {
        super(cause);
    }
}
