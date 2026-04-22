package com.example.bookstack_backend.dto.response;

import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.Collection;

public class UserDetailsResponse {

    private String username;

    private String email;

    private Collection<? extends GrantedAuthority> authorities;

    private BigDecimal balance;

    private Long id;

    public UserDetailsResponse(String username, String email,
                               Collection<? extends GrantedAuthority> authorities,
                               BigDecimal balance, Long id) {
        this.username = username;
        this.email = email;
        this.authorities = authorities;
        this.balance = balance;
        this.id = id;
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

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
