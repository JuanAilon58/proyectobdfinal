package com.proyecto.sistema.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @Column(name = "product_id", unique = true, nullable = false)
    private Long productId;

    @Column(name = "available_stock", nullable = false)
    private Integer available_stock;

    @Column(name = "reserved_stock")
    private Integer reserved_stock = 0;

    @Column(name = "updated_at")
    private LocalDateTime updated_at = LocalDateTime.now();
}
