package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.Role;

import java.util.List;

public class UserInfoResponse {

    private Long userId;

    private String username;

    private String email;

    private List<String> roles;

    public UserInfoResponse(Long userId, String username, String email, List<String> roles)  {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUsername() { return username; }
    public void setUsername(String username) {this.username = username;}

    public String getEmail() { return email; }
    public void setEmail(String email) {this.email = email;}

    public List<String> getRoles() {return roles; }
    public void setRoles(List<String> roles) {this.roles = roles; }
}
