package com.proyecto.sistema.service;

import com.proyecto.sistema.entity.Payment;
import com.proyecto.sistema.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    private final String INVENTORY_SERVICE_URL = "http://localhost:8083/api/inventory";

    @Transactional
    public Payment processPayment(Long orderId, BigDecimal amount, Long productId, Integer quantity) {
        // Simulación de procesamiento de pago
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentStatus("SUCCESS");
        payment.setTransactionReference(UUID.randomUUID().toString());
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Confirmar Stock en inventory-service tras pago exitoso
        String confirmUrl = INVENTORY_SERVICE_URL + "/confirm?productId=" + productId + "&quantity=" + quantity;
        try {
            restTemplate.postForEntity(confirmUrl, null, Void.class);
        } catch (Exception e) {
            // En un entorno real, manejaríamos esto con reintentos o eventos (Kafka)
            System.err.println("Advertencia: No se pudo confirmar el stock automáticamente: " + e.getMessage());
        }

        return savedPayment;
    }
}
