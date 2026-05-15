package com.proyecto.sistema.controller;

import com.proyecto.sistema.entity.Payment;
import com.proyecto.sistema.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Payment> process(
            @RequestParam Long orderId,
            @RequestParam BigDecimal amount,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        
        return ResponseEntity.ok(paymentService.processPayment(orderId, amount, productId, quantity));
    }
}
