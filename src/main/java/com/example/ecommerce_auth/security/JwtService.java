package com.example.ecommerce_auth.security;

import com.example.ecommerce_auth.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Date;

@Component
public class JwtService {

    private final Environment env;
    private Key signingKey;
    private long expirationMs;

    public JwtService(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        try {
            String rawSecret = env.getProperty("app.security.jwt-secret", "default_jwt_secret");

            // Derive 256-bit key from secret (ensures correct length)
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(rawSecret.getBytes(StandardCharsets.UTF_8));
            signingKey = Keys.hmacShaKeyFor(keyBytes);

            String expProp = env.getProperty("app.security.jwt-expiration-ms", "3600000").trim();
            expirationMs = Long.parseLong(expProp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JwtService", e);
        }
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("name", user.getName())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey) // ✅ fixed: use signingKey
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
