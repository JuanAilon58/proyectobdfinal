package com.proyecto.sistema.listener;

import com.proyecto.sistema.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void handlePaymentEvent(String message) {
        log.info("Evento de pago recibido: {}", message);
        // Supongamos que el mensaje es el ID de la orden
        notificationService.sendEmail(message, "PAGADA Y CONFIRMADA");
    }

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void handleOrderEvent(String message) {
        log.info("Evento de orden recibido: {}", message);
        notificationService.sendEmail(message, "CREADA - PENDIENTE DE PAGO");
    }
}
