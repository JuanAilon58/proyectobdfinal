package com.proyecto.sistema.controller;

import com.proyecto.sistema.entity.Order;
import com.proyecto.sistema.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(
            @RequestParam String customerId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        
        try {
            Order order = orderService.createOrder(customerId, productId, quantity);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
