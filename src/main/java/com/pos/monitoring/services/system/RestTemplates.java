package com.pos.monitoring.services.system;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface RestTemplates {

    <T> ResponseEntity<T> execute(String url, HttpMethod method, Map<String, String> headerData, Map<String, Object> body, Class<T> t);
}
