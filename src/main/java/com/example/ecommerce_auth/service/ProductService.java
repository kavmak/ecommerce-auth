package com.example.ecommerce_auth.service;

import com.example.ecommerce_auth.model.Product;
import com.example.ecommerce_auth.repository.ProductRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public Page<Product> getAllProducts(int page, int size) {
        // Simulate failure
       // throw new RuntimeException("Simulated database failure");
         return productRepository.findAll(PageRequest.of(page, size));
    }

    //  Must match method signature + Throwable
    public Page<Product> productFallback(int page, int size, Throwable throwable) {
        System.out.println(" Circuit breaker fallback triggered: " + throwable.getMessage());
        return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
