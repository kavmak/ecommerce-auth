# Circuit Breaker Integration Guide for E-Commerce Microservices


## Link for API Documentation
   https://documenter.getpostman.com/view/30029854/2sB3QQJ82A
   
## Overview

- The **Circuit Breaker** pattern helps microservices handle failures gracefully. 
- It prevents one failing dependency (like `inventory-service` or `payment-service`) from causing cascading failures across your   system.
- This project uses **Resilience4j**, a modern, lightweight fault-tolerance library compatible with **Spring Boot 3** and **JDK 21**.

## Key Features
-  **Automatic failure detection** — monitors failed calls and latency. 
-  **Circuit states management** —  
  -  **Closed** → normal operations  
  -  **Open** → requests blocked temporarily due to failures  
  -  **Half-Open** → test requests to check recovery 
-  **Fallback mechanism** — gracefully handles failures by returning predefined responses(Here empty page without any errors).
-  **Metrics exposure** — integrates with **Spring Boot Actuator** for health monitoring. 

---

##  1. Add Dependencies
    In your `pom.xml`:


<!-- Resilience4j for Circuit Breaker -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>

<!-- Optional: Actuator for monitoring -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

## 2. Configure application.properties

# Circuit Breaker Configuration
    resilience4j.circuitbreaker.instances.externalService.registerHealthIndicator=true
    resilience4j.circuitbreaker.instances.externalService.slidingWindowSize=5
    resilience4j.circuitbreaker.instances.externalService.failureRateThreshold=50
    resilience4j.circuitbreaker.instances.externalService.waitDurationInOpenState=10s
    resilience4j.circuitbreaker.instances.externalService.permittedNumberOfCallsInHalfOpenState=2
    resilience4j.circuitbreaker.instances.externalService.minimumNumberOfCalls=3
    resilience4j.circuitbreaker.instances.externalService.automaticTransitionFromOpenToHalfOpenEnabled=true

# Optional: Actuator endpoints
    management.endpoints.web.exposure.include=health,metrics,info

## 3. Implement Circuit Breaker in a Service

# Sample Code(Not in this project)
    @Service
    public class OrderService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String INVENTORY_SERVICE = "externalService";

<!-- Circuit breaker annotation used -->
@CircuitBreaker(name = INVENTORY_SERVICE, fallbackMethod = "fallbackCheckStock")
    public String checkProductStock(String productId) {
        String url = "http://localhost:8082/api/inventory/" + productId;
        return restTemplate.getForObject(url, String.class);
    }

 <!-- Fallback method when circuit is OPEN or call fails -->
    public String fallbackCheckStock(String productId, Throwable throwable) {
        return "Inventory service temporarily unavailable. Please retry later.";
    }
}
## 4. Monitor Circuit Breaker Health
 
# Hit api : GET http://localhost:8080/actuator/health



## 5. Test the Circuit Breaker

   Stop the target dependency or trigger an error.

   Call your API multiple times (≥5, per configuration).

# Circuit opens → requests instantly trigger the fallback.

# After waitDurationInOpenState, circuit moves to Half-Open and retries a few calls.

# If successful → back to Closed.
