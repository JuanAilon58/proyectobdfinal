package com.proyecto.sistema.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendEmail(String orderId, String status) {
        log.info("ENVIANDO NOTIFICACIÓN: La orden {} ha cambiado su estado a {}.", orderId, status);
        // Aquí iría la integración real con un servidor SMTP o servicio como SendGrid
    }
}
