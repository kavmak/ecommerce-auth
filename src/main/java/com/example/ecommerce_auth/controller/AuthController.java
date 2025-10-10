package com.example.ecommerce_auth.controller;

import com.example.ecommerce_auth.dto.LoginRequest;
import com.example.ecommerce_auth.dto.RegisterRequest;
import com.example.ecommerce_auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService s) {
        this.authService = s;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> res = authService.register(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> res = authService.login(request);
        return ResponseEntity.ok(res);
    }
}
