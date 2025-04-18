package com.backend.security;

import com.backend.service.custom.user.details.CustomCredentials;
import com.backend.service.custom.user.details.CustomUserDetails;
import com.backend.service.token.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static java.util.Collections.singletonMap;

public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final TokenService tokenService;

    public CustomAuthenticationFilter(String loginUrl, AuthenticationManager authManager, TokenService tokenService) {
        super(new AntPathRequestMatcher(loginUrl, "POST"));
        setAuthenticationManager(authManager);
        this.tokenService = tokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException {
        CustomCredentials credentials = new ObjectMapper().readValue(request.getInputStream(), CustomCredentials.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {
        SecurityContextHolder.getContext()
                .setAuthentication(authResult);
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String token = tokenService.generateToken(userDetails, authResult.getAuthorities());
        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(singletonMap("token", token)));
        response.setStatus(200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
