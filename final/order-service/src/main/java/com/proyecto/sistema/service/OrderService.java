package com.proyecto.sistema.service;

import com.proyecto.sistema.entity.Order;
import com.proyecto.sistema.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${services.inventory.url:http://inventory-service:8083/api/inventory}")
    private String inventoryServiceUrl;

    @Value("${services.catalog.url:http://catalog-service:8081/api/catalog}")
    private String catalogServiceUrl;

    @Transactional
    public Order createOrder(String customerIdentifier, Long productId, Integer quantity) {
        // 1. Obtener precio (En un entorno real llamaríamos a catalog-service o vendría en el request)
        BigDecimal price = BigDecimal.valueOf(1299.99);

        // 2. Crear y Guardar la Orden en estado PENDING (Esperando validación de inventario)
        Order order = new Order();
        order.setCustomerIdentifier(customerIdentifier);
        order.setOrderStatus("PENDING");
        order.setTotalAmount(price.multiply(BigDecimal.valueOf(quantity)));
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 3. Publicar evento para procesamiento asíncrono (Inventario -> Pago -> Envío)
        // Incluimos productId y quantity en el mensaje para el inventario
        String message = String.format("ORDER_CREATED:%d:%d:%d", savedOrder.getOrderId(), productId, quantity);
        kafkaTemplate.send("order-events", message);

        return savedOrder;
    }

    public Order fallbackReserveStock(String customerIdentifier, Long productId, Integer quantity, Throwable t) {
        throw new RuntimeException("El sistema de órdenes está experimentando dificultades. Inténtelo más tarde.");
    }
}

@Configuration
class RestConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
