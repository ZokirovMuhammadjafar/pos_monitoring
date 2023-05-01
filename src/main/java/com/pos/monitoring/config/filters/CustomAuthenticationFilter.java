package com.pos.monitoring.config.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.utils.DefaultUser;
import com.pos.monitoring.utils.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtil util;
    public CustomAuthenticationFilter(JWTUtil util) {
        this.util = util;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                DecodedJWT jwt = util.verifierAccess().verify(token);
                String username = jwt.getClaim("username").as(String.class);
                DefaultUser user =new DefaultUser(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (RuntimeException e) {
                throw new ValidatorException(e);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }


}
