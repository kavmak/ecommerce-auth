package com.example.ecommerce_auth.controller;

import com.example.ecommerce_auth.model.CartItem;
import com.example.ecommerce_auth.model.Order;
import com.example.ecommerce_auth.repository.OrderRepository;
import com.example.ecommerce_auth.security.JwtService;
import com.example.ecommerce_auth.util.AESUtil;
import com.example.ecommerce_auth.util.MD5Util;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepo;
    private final JwtService jwtService;
    private final AESUtil aesUtil; // ✅ added instance of AESUtil

    public OrderController(OrderRepository orderRepo, JwtService jwtService, AESUtil aesUtil) {
        this.orderRepo = orderRepo;
        this.jwtService = jwtService;
        this.aesUtil = aesUtil;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> payload) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.getEmailFromToken(token);

           
            String address = (String) payload.get("address");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) payload.get("items");

            List<CartItem> items = new ArrayList<>();
            double total = 0.0;

            for (Map<String, Object> item : itemsData) {
                CartItem ci = new CartItem();
                ci.setProductId((String) item.get("productId"));
                ci.setQuantity(((Number) item.get("quantity")).intValue());
                ci.setPrice(((Number) item.get("price")).doubleValue());
                items.add(ci);
                total += ci.getQuantity() * ci.getPrice();
            }

            Order order = new Order();
            order.setUserEmail(email);
            order.setItems(items);
            order.setTotalAmount(total);
            order.setEncryptedAddress(aesUtil.encrypt(address)); 

            Order saved = orderRepo.save(order);

            String checksum = MD5Util.generateChecksum(saved.getId() + saved.getUserEmail() + saved.getTotalAmount());
            Map<String, Object> response = Map.of(
                    "message", "Order placed successfully",
                    "orderId", saved.getId(),
                    "checksum", checksum
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.ETAG, checksum)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getOrderHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.getEmailFromToken(token);

        List<Order> orders = orderRepo.findByUserEmail(email);
        String combined = orders.toString();
        String checksum = MD5Util.generateChecksum(combined);

        return ResponseEntity.ok()
                .header(HttpHeaders.ETAG, checksum)
                .body(orders);
    }
}
