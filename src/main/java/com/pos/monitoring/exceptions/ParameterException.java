package com.pos.monitoring.exceptions;

import lombok.Getter;

@Getter
public class ParameterException extends RuntimeException {
    private final Object[] params = new Object[3];

    public ParameterException(String message, String entityName, String paramName, String paramValue) {
        super(message);
        params[0] = entityName;
        params[1] = paramName;
        params[2] = paramValue;
    }

    public ParameterException(String message,String entityName, String paramName) {
        super(message);
        params[0] = entityName;
        params[1] = paramName;
    }
}
