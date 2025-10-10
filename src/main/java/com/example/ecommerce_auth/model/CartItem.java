package com.example.ecommerce_auth.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "cart_items")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    private String id;

    private String userId;   
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;
}
