//package com.pos.monitoring.services.system.impl;
//
//import com.pos.monitoring.exceptions.ErrorCode;
//import com.pos.monitoring.exceptions.LocalizedApplicationException;
//import com.pos.monitoring.services.system.RestTemplates;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class RestTemplateBean implements RestTemplates {
//
////    private static String sessionToken;
////
////    @Value("${pos.monitoring.user.username}")
////    private String username;
////
////    @Value("${pos.monitoring.user.password}")
////    private String password;
////
////    @Value("${pos.monitoring.login.url}")
////    private String loginUrl;
//
////    private final RestTemplate restTemplate;
////
////    private Long sessionExpiration = System.currentTimeMillis();
////
////
////    public <T> ResponseEntity<T> execute(String url, HttpMethod method, Map<String, String> headerData, Map<String, Object> body, Class<T> t) {
////        String token = "login()";
////        headerData.put("Authorization", "Bearer " + token);
////
////        HttpEntity<Map<String, Object>> request = generateRequest(headerData, body);
////        try {
////            return restTemplate.exchange(url, method, request, t);
////        } catch (Exception e) {
////            throw new LocalizedApplicationException(ErrorCode.SERVER_ERROR_FROM8005, e.getCause());
////        }
////    }
////
////    private HttpEntity<Map<String, Object>> generateRequest(Map<String, String> headerData, Map<String, Object> body) {
////        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
////        headerData.keySet().forEach(key -> headers.add(key, headerData.get(key)));
////        Map<String, Object> params = new HashMap<>();
////        body.keySet().forEach(key -> params.put(key, body.get(key)));
////        return new HttpEntity<>(params, headers);
////    }
//
//    /*private String login() {
//        if (ObjectUtils.isEmpty(sessionToken) || sessionExpiration <= System.currentTimeMillis() - 1000) {
//            Map<String, String> headerData = new HashMap<>();
//            headerData.put(CONTENT_TYPE, "application/json-patch+json");
//            Map<String, Object> body = new HashMap<>();
//            body.put("userName", username);
//            body.put("password", password);
//
//            HttpEntity<Map<String, Object>> request = generateRequest(headerData, body);
//            ResponseEntity<LoginDto> response = restTemplate.exchange(loginUrl, HttpMethod.POST, request, LoginDto.class);
//
//            LoginDto loginDto = response.getBody();
//
//            if (ObjectUtils.isEmpty(loginDto)) {
//                throw new LocalizedApplicationException(ErrorCode.LOGIN_WITH_ATM);
//            }
//            sessionToken = loginDto.getToken();
//            sessionExpiration = loginDto.getExpiration();
//            return loginDto.getToken();
//        } else {
//            return sessionToken;
//        }
//    }*/
//}
