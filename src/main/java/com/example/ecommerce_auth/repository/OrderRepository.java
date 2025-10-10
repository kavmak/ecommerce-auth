package com.example.ecommerce_auth.repository;

import com.example.ecommerce_auth.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserEmail(String email);
}
