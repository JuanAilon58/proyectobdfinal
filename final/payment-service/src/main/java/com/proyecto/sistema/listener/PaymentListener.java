package com.proyecto.sistema.listener;

import com.proyecto.sistema.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void handleOrderEvent(String message) {
        log.info("Evento de orden recibido en Payment: {}", message);
        
        if (message.startsWith("ORDER_CREATED:")) {
            try {
                // Formato: ORDER_CREATED:orderId:productId:quantity
                String[] parts = message.split(":");
                Long orderId = Long.parseLong(parts[1]);
                
                // Simulación: En un caso real, obtendríamos los detalles de la orden
                paymentService.processPayment(orderId, BigDecimal.valueOf(1299.99), 1L, 1);
                
                // Publicar evento de pago exitoso
                kafkaTemplate.send("payment-events", "PAYMENT_SUCCESS:" + orderId);
                log.info("Pago procesado exitosamente para la orden: {}", orderId);
            } catch (Exception e) {
                log.error("Error al procesar el pago para el evento {}: {}", message, e.getMessage());
            }
        }
    }
}
