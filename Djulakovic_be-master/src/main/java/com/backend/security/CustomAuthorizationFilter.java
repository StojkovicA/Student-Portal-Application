package com.backend.security;

import com.backend.service.custom.user.details.CustomUserDetailsService;
import com.backend.service.token.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.io.IOException;

public class CustomAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "authorization";
    private final TokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;

    public CustomAuthorizationFilter(String url, CustomUserDetailsService customUserDetailsService,
                                     TokenService tokenService) {
        super(url);
        this.customUserDetailsService = customUserDetailsService;
        this.tokenService = tokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String token = null;
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(7);
        }

        try {
            return tokenService.getAuthentication(token, customUserDetailsService);
        } catch (ExpiredJwtException e) {
            throw new SessionAuthenticationException("Token expired");
        } catch (JwtException e) {
            throw new SessionAuthenticationException("bad token");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext()
                .setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.sendError(401, failed.getMessage());
    }
}
