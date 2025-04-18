package com.backend.service.custom.user.details;

import com.backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User implements UserDetails {

    private String credentials;
    private String sessionIdentifier;

    @Builder
    public CustomUserDetails(String credentials,
                             String sessionIdentifier,
                             User user) {
        super(user);
        this.credentials = credentials;
        this.sessionIdentifier = sessionIdentifier;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roles = StringUtils.collectionToCommaDelimitedString(getActualRoles());
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);

    }

    @Override
    public String getUsername() {
        return super.getFullName();
    }

    @Override
    public boolean isAccountNonExpired() {
       return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
