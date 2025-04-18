package com.backend.service.token;

import com.backend.domain.token.Token;
import com.backend.domain.token.TokenRepository;
import com.backend.service.custom.user.details.CustomUserDetails;
import com.backend.service.custom.user.details.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.backend.security.CustomAuthorizationFilter.AUTH_HEADER;
import static com.backend.security.CustomAuthorizationFilter.TOKEN_PREFIX;
import static com.backend.utils.ContextHolderUtil.getOptionalUserDetails;

@Service
public class TokenService {

    private static final byte[] NEW_SECRET = "cd+Pr1js+w2qfT2BoCD+tPcYp9LbjpmhSMEJqUob1mcxZ7+Wmik4AYdjX+DlDjmE4yporzQ9tm7v3z/j+QbdYg==".getBytes(StandardCharsets.UTF_8);
    private static final SecretKey key = Keys.hmacShaKeyFor(NEW_SECRET);
    private static final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>(); // key - token,  value - credentials

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    @Transactional
    public String generateToken(CustomUserDetails customUserDetails,
                                Collection<? extends GrantedAuthority> authorities) {
        return generateJwtToken(customUserDetails, authorities);
    }

    public Authentication getAuthentication(String token, CustomUserDetailsService customUserDetailsService) {
        UsernamePasswordAuthenticationToken authToken = null;
        if (token != null) {
            String credentials = getCredentials(token);
            if (credentials != null) {
                CustomUserDetails customUserDetails = (CustomUserDetails) loadUserDetails(customUserDetailsService, credentials);
                customUserDetails.setSessionIdentifier(customUserDetails.getSessionIdentifier());
                String roles = customUserDetails.getActualRoles()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                customUserDetails.setSessionIdentifier(customUserDetails.getCredentials());
                authToken = new UsernamePasswordAuthenticationToken(credentials, null, AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
                authToken.setDetails(customUserDetails);
            }
        }
        if (authToken == null) {
            throw new JwtException("Token is bad");
        }
        return authToken;
    }

    @Transactional
    public void logoutUser(HttpServletRequest request) {
        String token = null;
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(7);
        }
        if (token != null) {
            removeToken(token);
        }
    }

    private String generateJwtToken(CustomUserDetails customUserDetails,
                                    Collection<? extends GrantedAuthority> authorities) {
        String roles = StringUtils.collectionToCommaDelimitedString(AuthorityUtils.authorityListToSet(authorities));
        String credentials = customUserDetails.getCredentials()
                .toLowerCase();

        String token = Jwts.builder()
                .setSubject(customUserDetails.getCredentials())
                .claim("roles", roles)
                .setId(UUID.randomUUID()
                        .toString())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(key)
                .setIssuer(new Random(100000L).nextInt() + "")
                .compact();

        saveToken(token, credentials);
        return token;
    }

    private UserDetails loadUserDetails(CustomUserDetailsService customUserDetailsService, String credentials) {
        Optional<CustomUserDetails> maybeUser = getOptionalUserDetails();
        boolean oldUser = maybeUser.isPresent() && Objects.equals(maybeUser.get()
                .getCredentials(), credentials);
        return oldUser ? maybeUser.get() : customUserDetailsService.loadUserByUsername(credentials);
    }

    private void saveToken(String token, String credentials) {
        activeTokens.put(token, credentials);
        tokenRepository.save(new Token(token, credentials));
    }

    private String getCredentials(String token) {
        return activeTokens.get(token);
    }

    private void removeToken(String token) {
        activeTokens.remove(token);
        tokenRepository.deleteAllByToken(token);
    }

    public void restoreTokens() {
        List<Token> tokens = tokenRepository.findAll();
        tokens.forEach(token -> activeTokens.put(token.getToken(), token.getCredentials()));
    }
}
