package com.proyecto.sistema.controller;

import com.proyecto.sistema.model.CartItem;
import com.proyecto.sistema.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{customerId}")
    public ResponseEntity<Void> addToCart(@PathVariable String customerId, @RequestBody CartItem item) {
        cartService.addItem(customerId, item);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable String customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clear(@PathVariable String customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok().build();
    }
}
