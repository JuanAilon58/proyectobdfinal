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
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackReserveStock")
    public Order createOrder(String customerIdentifier, Long productId, Integer quantity) {
        // 1. Reservar Stock en inventory-service (Ahora optimizado con Redis en el destino)
        String reserveUrl = inventoryServiceUrl + "/reserve?productId=" + productId + "&quantity=" + quantity;
        ResponseEntity<String> response = restTemplate.postForEntity(reserveUrl, null, String.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("No se pudo reservar el stock: " + response.getBody());
        }

        // 2. Obtener precio (En un entorno real llamaríamos a catalog-service)
        BigDecimal price = BigDecimal.valueOf(1299.99);

        // 3. Crear y Guardar la Orden
        Order order = new Order();
        order.setCustomerIdentifier(customerIdentifier);
        order.setOrderStatus("CREATED");
        order.setTotalAmount(price.multiply(BigDecimal.valueOf(quantity)));
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 4. Publicar evento para procesamiento asíncrono (Pago, Envío, etc.)
        kafkaTemplate.send("order-events", "ORDER_CREATED:" + savedOrder.getOrderId());

        return savedOrder;
    }

    public Order fallbackReserveStock(String customerIdentifier, Long productId, Integer quantity, Throwable t) {
        throw new RuntimeException("El servicio de inventario no está disponible actualmente. Inténtelo más tarde.");
    }
}

@Configuration
class RestConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
