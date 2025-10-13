package com.example.ecommerce_auth.controller;

import com.example.ecommerce_auth.model.Product;
import com.example.ecommerce_auth.service.ProductService;
import com.example.ecommerce_auth.util.MD5Util;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    private static final String PRODUCT_SERVICE = "productService";

    // ✅ Circuit Breaker added here
    @GetMapping
    @CircuitBreaker(name = PRODUCT_SERVICE, fallbackMethod = "getAllProductsFallback")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> productPage = productService.getAllProducts(page, size);
        String jsonResponse = productPage.getContent().toString();
        String eTag = MD5Util.generateChecksum(jsonResponse);

        return ResponseEntity.ok()
                .eTag(eTag)
                .body(productPage);
    }

    // ✅ Fallback method when circuit is open or service fails
    public ResponseEntity<?> getAllProductsFallback(int page, int size, Throwable throwable) {
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("message", "Product service is currently unavailable. Please try again later.");
        fallbackResponse.put("fallback", true);
        fallbackResponse.put("error", throwable.getMessage());

        // return simple fallback response
        return ResponseEntity.ok(fallbackResponse);
    }

    // 2. Get Product Details by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();
        String checksum = MD5Util.generateChecksum(product.toString());

        return ResponseEntity.ok()
                .header("X-Checksum", checksum)
                .body(product);
    }

    // 3. Create New Product
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);

        String checksum = MD5Util.generateChecksum(savedProduct.toString());
        return ResponseEntity.ok()
                .header("X-Checksum", checksum)
                .body(savedProduct);
    }
}
