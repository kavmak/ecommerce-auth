package com.example.ecommerce_auth.controller;

import com.example.ecommerce_auth.model.CartItem;
import com.example.ecommerce_auth.service.CartService;
import com.example.ecommerce_auth.security.JwtService;
import com.example.ecommerce_auth.util.MD5Util;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final JwtService jwtService;

    public CartController(CartService cartService, JwtService jwtService) {
        this.cartService = cartService;
        this.jwtService = jwtService;
    }

    //  Add or Update Cart Item
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody CartItem item) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUsername(token);

        CartItem savedItem = cartService.addOrUpdateItem(userId, item);
        String checksum = MD5Util.generateChecksum(savedItem.toString());

        return ResponseEntity.ok()
                .header("X-Checksum", checksum)
                .body(savedItem);
    }

    // Get User Cart Items
    @GetMapping
    public ResponseEntity<?> getUserCart(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUsername(token);

        List<CartItem> cartItems = cartService.getUserCart(userId);
        double totalAmount = cartService.calculateTotal(cartItems);

        Map<String, Object> response = new HashMap<>();
        response.put("cartItems", cartItems);
        response.put("totalAmount", totalAmount);

        String checksum = MD5Util.generateChecksum(response.toString());

        return ResponseEntity.ok()
                .header("X-Checksum", checksum)
                .body(response);
    }
}
