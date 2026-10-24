package com.rahul.fluxlimit.controller;

import com.rahul.fluxlimit.annotation.RateLimit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    // Sirf annotation lagaya, capacity 5 aur refill 1 token/sec
    @GetMapping("/order")
    @RateLimit(capacity = 5, refillRate = 1)
    public ResponseEntity<String> placeOrder() {
        return ResponseEntity.ok("Order placed successfully!");
    }
}