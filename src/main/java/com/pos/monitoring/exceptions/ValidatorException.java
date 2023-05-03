package com.pos.monitoring.exceptions;

public class ValidatorException extends RuntimeException{
    public ValidatorException(String message) {
        super(message);
    }

    public ValidatorException(RuntimeException e) {
        super(e);
    }

    public ValidatorException(IllegalAccessException e) {
        super(e);
    }
}
