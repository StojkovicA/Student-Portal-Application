package com.backend.utils;

import com.backend.domain.user.User;
import com.backend.service.custom.user.details.CustomUserDetails;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static java.lang.String.valueOf;

public class ContextHolderUtil {

    public static Optional<CustomUserDetails> getOptionalUserDetails() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof CustomUserDetails)) {
            return Optional.empty();
        }
        return Optional.of((CustomUserDetails) auth.getDetails());
    }

    public static CustomUserDetails getUserDetails() {
        return getOptionalUserDetails()
                .orElseThrow(() -> new IllegalStateException("Not logged in properly"));
    }

    public static User getUser() {
        return getOptionalUserDetails()
                .orElseThrow(() -> new IllegalStateException("Not logged in properly"));
    }

    public static String getSessIdentifier() {
        String sessionIdentifier = getSessionIdentifier();
        if (StringUtils.isEmpty(sessionIdentifier)) {
            throw new IllegalStateException("Cannot decrypt private key without session identifier. User is probably not logged in.");
        }
        return sessionIdentifier;
    }

    public static String getSessionIdentifier() {
        return getOptionalUserDetails()
                .map(userDetails -> valueOf(userDetails.getSessionIdentifier()))
                .orElse("");
    }
}
