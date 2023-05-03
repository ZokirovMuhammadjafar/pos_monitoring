package com.pos.monitoring.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Getter
@Component
public class JWTUtil {
    @Value("${jwt.expire}")
    private int expire;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.refresh_expire}")
    private int refresh_expire;
    @Value("${jwt.issuer_access}")
    private String issuerAccess;
    @Value("${jwt.issuer_refresh}")
    private String issuerRefresh;
    public Date getExpireDate(){
        return new Date((expire*10L)+System.currentTimeMillis());
    }
    public  Date getExpireDateForRefreshToken(){
        return new Date(refresh_expire+System.currentTimeMillis());
    }
    public Algorithm getAlgorithm(){
        return Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
    }

    public JWTVerifier verifierAccess() {
        return JWT.require(getAlgorithm()).withSubject("access").withIssuer(issuerAccess).build();
    }
    public  JWTVerifier verifierRefresh() {
        return JWT.require(getAlgorithm()).withSubject("refresh").withIssuer(issuerRefresh).build();
    }

}