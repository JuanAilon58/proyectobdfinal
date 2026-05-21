package com.proyecto.sistema.listener;

import com.proyecto.sistema.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryListener {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void handleOrderEvent(String message) {
        log.info("Evento de orden recibido en Inventory: {}", message);
        
        if (message.startsWith("ORDER_CREATED:")) {
            try {
                // Formato: ORDER_CREATED:orderId:productId:quantity
                String[] parts = message.split(":");
                Long orderId = Long.parseLong(parts[1]);
                Long productId = Long.parseLong(parts[2]);
                Integer quantity = Integer.parseInt(parts[3]);
                
                boolean reserved = inventoryService.reserveProduct(productId, quantity);
                
                if (reserved) {
                    log.info("Stock reservado en Redis para la orden: {}", orderId);
                    // Opcional: Enviar evento INVENTORY_RESERVED para que Payment continúe
                } else {
                    log.warn("No hay stock suficiente para la orden: {}", orderId);
                    // Opcional: Enviar evento INVENTORY_FAILED para cancelar la orden
                }
            } catch (Exception e) {
                log.error("Error al procesar reserva para el evento {}: {}", message, e.getMessage());
            }
        }
    }

    @KafkaListener(topics = "payment-events", groupId = "inventory-group")
    public void handlePaymentEvent(String message) {
        log.info("Evento de pago recibido en Inventory: {}", message);
        
        if (message.startsWith("PAYMENT_SUCCESS:")) {
            try {
                Long orderId = Long.parseLong(message.substring("PAYMENT_SUCCESS:".length()));
                
                // Simulación: En un caso real, obtendríamos el productId y quantity de la orden.
                // Aquí usamos valores fijos para el demo o los buscaríamos.
                // Por ahora, confirmamos el stock del producto 1 (el del flash sale).
                inventoryService.confirmPurchase(1L, 1);
                
                log.info("Stock confirmado en DB para la orden: {}", orderId);
            } catch (Exception e) {
                log.error("Error al confirmar stock para el evento {}: {}", message, e.getMessage());
            }
        }
    }
}
