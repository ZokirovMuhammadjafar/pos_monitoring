package com.pos.monitoring.exceptions;

import java.util.Objects;

public class ValidatorException extends RuntimeException{
    public ValidatorException(String message) {
        super(message);
    }
}
