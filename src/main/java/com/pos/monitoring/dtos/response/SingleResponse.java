package com.pos.monitoring.dtos.response;



import com.pos.monitoring.dtos.base.Response;

import java.io.Serializable;

public class SingleResponse<T> extends Response implements Serializable {
    private T data;

    private SingleResponse(T data) {
        this.data = data;
    }

    public SingleResponse(int code, T data) {
        super(code);
        this.data = data;
    }

    private SingleResponse() {
    }
    public SingleResponse(String error,int code) {
        super();
        add("error",error);
    }

    public static <T> SingleResponse of(T data) {
        return new SingleResponse(data);
    }

    public static <T> SingleResponse empty() {
        return new SingleResponse();
    }

    public boolean isSuccess() {
        return errors == null || errors.isEmpty();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}