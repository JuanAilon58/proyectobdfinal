package com.proyecto.sistema.service;

import com.proyecto.sistema.entity.Order;
import com.proyecto.sistema.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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

    // URLs de los otros microservicios (en un entorno real vendrían de un Config Server o Eureka)
    private final String INVENTORY_SERVICE_URL = "http://localhost:8083/api/inventory";
    private final String CATALOG_SERVICE_URL = "http://localhost:8081/api/catalog";

    @Transactional
    public Order createOrder(String customerIdentifier, Long productId, Integer quantity) {
        // 1. Reservar Stock en inventory-service
        String reserveUrl = INVENTORY_SERVICE_URL + "/reserve?productId=" + productId + "&quantity=" + quantity;
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(reserveUrl, null, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("No se pudo reservar el stock");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de inventario: " + e.getMessage());
        }

        // 2. Obtener precio del producto desde catalog-service (simplificado para el ejemplo)
        BigDecimal price = BigDecimal.valueOf(1299.99);

        // 3. Crear y Guardar la Orden
        Order order = new Order();
        order.setCustomerIdentifier(customerIdentifier);
        order.setOrderStatus("PENDING");
        order.setTotalAmount(price.multiply(BigDecimal.valueOf(quantity)));
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 4. Publicar evento (Patrón Outbox simplificado)
        try {
            kafkaTemplate.send("order-events", "ORDER_CREATED:" + savedOrder.getOrderId());
        } catch (Exception e) {
            System.err.println("Error al enviar evento a Kafka: " + e.getMessage());
        }

        return savedOrder;
    }
}

@Configuration
class RestConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
