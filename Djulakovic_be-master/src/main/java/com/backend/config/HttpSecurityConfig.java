package com.backend.config;

import com.backend.security.CustomAuthenticationFilter;
import com.backend.security.CustomAuthorizationFilter;
import com.backend.service.custom.user.details.CustomUserDetailsService;
import com.backend.service.token.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class HttpSecurityConfig {

    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    public HttpSecurityConfig(TokenService tokenService, PasswordEncoder passwordEncoder,
                              CustomUserDetailsService customUserDetailsService,
                              CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    @Order(BASIC_AUTH_ORDER + 1)
    public SecurityFilterChain backendFilterChain(HttpSecurity security) throws Exception {
        return security.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to Swagger URLs
                        .requestMatchers("/uploads/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**")
                        .permitAll()
                        // Allow public access to these paths
                        .requestMatchers("/data", "/login").permitAll()
                        // Securing specific paths
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterBefore(new CustomAuthenticationFilter("/login", authenticationManager(), tokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomAuthorizationFilter("/**", customUserDetailsService, tokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(webAuthenticationProvider())
                .authenticationManager(authenticationManager())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(management -> {
                    management.invalidSessionUrl("http://localhost:8080/error");
                    management.maximumSessions(-1);
                    management.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .build();
    }

    private DaoAuthenticationProvider webAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(true);
        return provider;
    }

    private AuthenticationManager authenticationManager() {
        return new ProviderManager(webAuthenticationProvider());
    }

}
