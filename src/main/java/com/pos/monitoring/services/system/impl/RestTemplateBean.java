package com.pos.monitoring.services.system.impl;

import com.pos.monitoring.exceptions.ErrorCode;
import com.pos.monitoring.exceptions.LocalizedApplicationException;
import com.pos.monitoring.services.system.RestTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestTemplateBean implements RestTemplates {

    @Value("${basic.username}")
    private String plumUsername;

    @Value("${basic.password}")
    private String plumPassword;

    private final RestTemplate restTemplate;

    @Override
    public synchronized <T> ResponseEntity<T> executeWithBasic(String url, HttpMethod method, Map<String, String> headerData, Map<String, Object> body, Class<T> t) {
        setHeader(headerData);

        HttpEntity<Map<String, Object>> request = generateRequest(headerData, body);
        try {
            return restTemplate.exchange(url, method, request, t);
        } catch (Exception e) {
            throw new LocalizedApplicationException(ErrorCode.SERVER_ERROR_FROM_PLUM, e.getCause());
        }
    }

    private void setHeader(Map<String, String> headerData) {
        String context = plumUsername + ":" + plumPassword;
        String basicToken = Base64.getEncoder().encodeToString(context.getBytes());
        headerData.put("Authorization", "Basic " + basicToken);
    }

    private HttpEntity<Map<String, Object>> generateRequest(Map<String, String> headerData, Map<String, Object> body) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headerData.keySet().forEach(key -> headers.add(key, headerData.get(key)));
        Map<String, Object> params = new HashMap<>();
        body.keySet().forEach(key -> params.put(key, body.get(key)));
        return new HttpEntity<>(params, headers);
    }
}
