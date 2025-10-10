package com.example.ecommerce_auth.service;

import com.example.ecommerce_auth.dto.*;
import com.example.ecommerce_auth.model.User;
import com.example.ecommerce_auth.repository.UserRepository;
import com.example.ecommerce_auth.security.JwtService;
import com.example.ecommerce_auth.util.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Instant;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;
    private final JwtService jwtService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       AESUtil aesUtil,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.aesUtil = aesUtil;
        this.jwtService = jwtService;
    }

    public Map<String, Object> register(RegisterRequest req) {
        Optional<User> existing = userRepository.findByEmail(req.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashed = passwordEncoder.encode(req.getPassword());
        String encryptedPhone = aesUtil.encrypt(req.getPhone());

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(hashed);
        user.setPhoneEncrypted(encryptedPhone);
        user.setCreatedAt(Instant.now());

        userRepository.save(user);

        // Build response without checksum
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", "success");
        resp.put("message", "User registered successfully");
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        resp.put("user", userData);

        // compute md5 checksum over current resp JSON (without checksum key)
        String checksum = computeMd5Hex(resp);
        resp.put("checksum", checksum);
        return resp;
    }

    public Map<String, Object> login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        // prepare response
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", "success");

        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        // return decrypted phone on login - be cautious in prod
        try {
            userData.put("phone", aesUtil.decrypt(user.getPhoneEncrypted()));
        } catch (Exception e) {
            userData.put("phone", null);
        }

        resp.put("token", token);
        resp.put("user", userData);

        String checksum = computeMd5Hex(resp);
        resp.put("checksum", checksum);
        return resp;
    }

    private String computeMd5Hex(Object obj) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(obj);
            return DigestUtils.md5DigestAsHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed computing checksum", e);
        }
    }
}
