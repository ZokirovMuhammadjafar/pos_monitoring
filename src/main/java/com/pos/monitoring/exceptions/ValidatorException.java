package com.pos.monitoring.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidatorException extends RuntimeException{
    Object[] objects;
    public ValidatorException(String message) {
        super(message);

    }
    public ValidatorException(String message,List<Object>params) {
        super(message);
        objects=params.toArray();
    }
    public ValidatorException(String message,Object ...args) {
        super(message);
        objects=args;
    }

    public ValidatorException(RuntimeException e) {
        super(e);
    }

    public ValidatorException(IllegalAccessException e) {
        super(e);
    }
}
