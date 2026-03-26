package com.example.bookstack_backend.dto.response;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserDetailsResponse {

    private String username;

    private String email;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsResponse(String username, String email, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthorities() {
        return authorities.toString();
    }
}
