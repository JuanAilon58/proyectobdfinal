package com.proyecto.sistema.repository;

import com.proyecto.sistema.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(Long orderId);
    Payment findByOrderId(Long orderId);
}
