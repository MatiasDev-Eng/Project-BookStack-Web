package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.Role;

import java.util.List;

public class UserInfoResponse {

    private Long userId;

    private String username;

    private String email;

    private String themePreference;



    public UserInfoResponse(Long userId, String username, String email, String themePreference)  {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.themePreference = themePreference;

    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUsername() { return username; }
    public void setUsername(String username) {this.username = username;}

    public String getEmail() { return email; }
    public void setEmail(String email) {this.email = email;}

    public String getThemePreference() {return themePreference;}
}
