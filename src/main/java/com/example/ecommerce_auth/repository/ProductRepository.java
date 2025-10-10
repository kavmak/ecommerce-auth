package com.example.ecommerce_auth.repository;

import com.example.ecommerce_auth.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
}
