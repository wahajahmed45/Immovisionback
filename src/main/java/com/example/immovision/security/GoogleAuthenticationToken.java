package com.example.immovision.security;

import com.example.immovision.entities.user.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleAuthenticationToken extends AbstractAuthenticationToken {
    private final User principal;

    public GoogleAuthenticationToken(User user) {
        super(getAuthoritiesFromUser(user));
        this.principal = user;
        setAuthenticated(true);
    }

    private static Collection<GrantedAuthority> getAuthoritiesFromUser(User user) {
        // Convert Role and ACLs to GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().getAcls().stream()
                .map(acl -> new SimpleGrantedAuthority(acl.getName()))
                .collect(Collectors.toList());

        // Add the role itself as an authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRoles().getName().toUpperCase()));

        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials needed for Google authentication
    }

    @Override
    public User getPrincipal() {
        return principal;
    }
}