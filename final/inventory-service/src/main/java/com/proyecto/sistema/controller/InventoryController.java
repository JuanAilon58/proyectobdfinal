package com.proyecto.sistema.controller;

import com.proyecto.sistema.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}/stock")
    public ResponseEntity<Integer> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getAvailableStock(productId));
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestParam Long productId, @RequestParam Integer quantity) {
        boolean success = inventoryService.reserveProduct(productId, quantity);
        if (success) {
            return ResponseEntity.ok("Reserva exitosa");
        } else {
            return ResponseEntity.badRequest().body("Stock insuficiente o producto no encontrado");
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestParam Long productId, @RequestParam Integer quantity) {
        inventoryService.confirmPurchase(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
