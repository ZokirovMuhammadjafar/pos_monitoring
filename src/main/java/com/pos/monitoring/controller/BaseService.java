package com.pos.monitoring.controller;

import com.auth0.jwt.JWT;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.utils.JWTUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BaseService {
    private final JWTUtil util;
    private static String PASSWORD="banking";
    private static String USERNAME="technology";

    @PostMapping(value = "/login")
    public SingleResponse login(@RequestBody LoginDto user) {
        if(user.password.equals(PASSWORD)&&user.username.equals(USERNAME)){
            return new SingleResponse(200, Map.of("token",accessToken(user.password(),-1L)));
        }else {
            return new SingleResponse("Authentication failed not found",400);
        }

    }
    private String accessToken(String username, Long id) {
        String access = JWT.create().withIssuer(util.getIssuerAccess()).withSubject("access").withExpiresAt(util.getExpireDate()).withClaim("username", username).withClaim("id", id).sign(util.getAlgorithm());
        return access;
    }

    record LoginDto(@NotNull String username, @NotNull String password){};
}
