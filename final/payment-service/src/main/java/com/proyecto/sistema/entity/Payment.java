package com.proyecto.sistema.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id", unique = true, nullable = false)
    private Long orderId;

    private BigDecimal amount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
