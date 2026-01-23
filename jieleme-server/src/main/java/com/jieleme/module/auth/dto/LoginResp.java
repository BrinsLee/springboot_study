package com.jieleme.module.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jieleme.module.user.entity.User;

/**
 * 登录响应
 */
public class LoginResp {
    
    private String token;
    
    @JsonProperty("expiresIn")
    private long expiresIn;
    
    private User user;
    
    // Constructors
    public LoginResp() {
    }
    
    public LoginResp(String token, long expiresIn, User user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
