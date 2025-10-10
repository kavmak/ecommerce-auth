package com.example.ecommerce_auth.service;

import com.example.ecommerce_auth.model.CartItem;
import com.example.ecommerce_auth.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public CartItem addOrUpdateItem(String userId, CartItem item) {
        CartItem existing = cartRepository.findByUserIdAndProductId(userId, item.getProductId());

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            existing.setTotalPrice(existing.getQuantity() * existing.getPrice());
            return cartRepository.save(existing);
        } else {
            item.setUserId(userId);
            item.setTotalPrice(item.getPrice() * item.getQuantity());
            return cartRepository.save(item);
        }
    }

    public List<CartItem> getUserCart(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public double calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }
}
