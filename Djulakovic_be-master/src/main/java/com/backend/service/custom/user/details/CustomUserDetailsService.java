package com.backend.service.custom.user.details;

import com.backend.domain.user.User;
import com.backend.service.user.UserReadService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    public static final String INVALID_CREDENTIALS = "Invalid email address or password";
    private final UserReadService userReadService;

    @Autowired
    public CustomUserDetailsService(UserReadService userReadService) {
        this.userReadService = userReadService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null) {
            throw new UsernameNotFoundException(INVALID_CREDENTIALS);
        }
        User user = userReadService.findByEmail(username, u -> {
            Hibernate.initialize(u.getRoles());
            Hibernate.initialize(u.getStudiesType());
            Hibernate.initialize(u.getMajor());
            Hibernate.initialize(u.getModule());
        });

        if (user == null || !user.isActive()) {
            throw new UsernameNotFoundException(INVALID_CREDENTIALS);
        }

        return CustomUserDetails.builder()
                .credentials(username)
                .user(user)
                .build();

    }
}
