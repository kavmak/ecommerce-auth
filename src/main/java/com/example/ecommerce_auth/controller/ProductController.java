package com.example.ecommerce_auth.controller;

import com.example.ecommerce_auth.model.Product;
import com.example.ecommerce_auth.service.ProductService;
import com.example.ecommerce_auth.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    //Get All Products Paginated + MD5 ETag
    @GetMapping
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

    // Create New Product ...public
@PostMapping
public ResponseEntity<?> createProduct(@RequestBody Product product) {
    Product savedProduct = productService.saveProduct(product);

    // Generate MD5 checksum for response
    String checksum = MD5Util.generateChecksum(savedProduct.toString());

    return ResponseEntity.ok()
            .header("X-Checksum", checksum)
            .body(savedProduct);
}

}
