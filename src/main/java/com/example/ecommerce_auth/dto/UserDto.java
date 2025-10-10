package com.example.ecommerce_auth.dto;

public class UserDto {
    private String id;
    private String name;
    private String email;
    private String phone; 

    public UserDto() {}

    public UserDto(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String i) { this.id = i; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPhone() { return phone; }
    public void setPhone(String p) { this.phone = p; }
}
