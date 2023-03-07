package com.pos.monitoring.exceptions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalizedApplicationException extends RuntimeException {

    private static final long serialVersionUID = -6619604892898952265L;

    private final ErrorCode errorCode;
    private final Serializable[] params;

    public LocalizedApplicationException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode.name() + ": [NO PARAMS]", throwable);
        this.errorCode = errorCode;
        this.params = new Serializable[0];
    }

    public LocalizedApplicationException(ErrorCode errorCode, List<Serializable> params) {
        super(errorCode.name() + ":" + Arrays.toString(params.toArray(new Serializable[0])));
        this.errorCode = errorCode;
        this.params = params.toArray(new Serializable[0]);
    }

    public LocalizedApplicationException(ErrorCode errorCode, Serializable param) {
        this(errorCode, Collections.singletonList(param));
    }

    public LocalizedApplicationException(ErrorCode errorCode) {
        super(errorCode.name() + ": [NO PARAMS]");
        this.errorCode = errorCode;
        this.params = new Serializable[0];
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Serializable[] getParams() {
        return params;
    }
}
