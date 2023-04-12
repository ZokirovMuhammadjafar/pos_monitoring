package com.pos.monitoring.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    protected boolean success;
    protected int code;
    protected Map<String, String> errors;

    public Response() {
    }

    public Response(int code) {
        this.code = code;
    }

    public Response add(String key, String value) {
        if (errors == null) errors = new HashMap<>();
        errors.put(key, value);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        if (errors == null)
            return "";
        StringBuilder builder = new StringBuilder();
        errors.forEach((key, value) -> {
            if ("message".equals(key))
                builder.append(value).append(";<br/>");
            else
                builder.append(key).append(": ").append(value).append(";<br/>");
        });
        return builder.toString();
    }
}