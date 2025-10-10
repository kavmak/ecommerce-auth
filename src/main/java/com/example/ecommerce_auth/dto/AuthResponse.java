package com.example.ecommerce_auth.dto;

public class AuthResponse {
    private String token;
    private UserDto user;

    public AuthResponse() {}

    public AuthResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }

    // getters & setters
    public String getToken() { return token; }
    public void setToken(String t) { token = t; }
    public UserDto getUser() { return user; }
    public void setUser(UserDto u) { user = u; }
}
