package com.example.ecommerce_auth;

import com.example.ecommerce_auth.model.Product;
import com.example.ecommerce_auth.repository.ProductRepository;
import com.example.ecommerce_auth.service.ProductService;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class EcommerceAuthApplicationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void resetCircuitBreaker() {
        // Reset circuit breaker state before each test
        circuitBreakerRegistry.circuitBreaker("productService").reset();
    }

    @Test
    void contextLoads() {
        // Ensures Spring context loads successfully
    }

    @Test
    void testGetAllProducts_SuccessfulCall() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        Page<Product> mockPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(PageRequest.of(0, 5))).thenReturn(mockPage);

        // Act
        Page<Product> result = productService.getAllProducts(0, 5);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findAll(PageRequest.of(0, 5));
    }

    @Test
    void testGetAllProducts_TriggersFallback() {
        // Arrange - simulate failure in repository
        when(productRepository.findAll(PageRequest.of(0, 5)))
                .thenThrow(new RuntimeException("Simulated DB failure"));

        // Act
        Page<Product> result = productService.getAllProducts(0, 5);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(productRepository, times(1)).findAll(PageRequest.of(0, 5));
    }
}
