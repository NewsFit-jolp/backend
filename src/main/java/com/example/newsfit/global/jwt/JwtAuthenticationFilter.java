package com.example.newsfit.global.jwt;

import com.example.newsfit.global.error.exception.CustomAccessDeniedException;
import com.example.newsfit.global.error.exception.CustomException;
import com.example.newsfit.global.error.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String token = tokenService.resolveToken(request);
            if (token != null && tokenService.validateToken(token)) {
                // check access token
                token = token.split(" ")[1].trim();
                Authentication auth = tokenService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }
        catch(Exception e){
            if(e instanceof CustomAccessDeniedException){
                CustomAccessDeniedException accessDeniedException = (CustomAccessDeniedException) e;
                handleAuthenticationException(response, accessDeniedException.getErrorCode(), accessDeniedException.getMessage());
            }
        }

    }

    private void handleAuthenticationException(HttpServletResponse response, ErrorCode errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.printf("{\"statusCode\": %s, \"message\": \"%s\"}", errorCode, message);
        out.flush();
    }
}