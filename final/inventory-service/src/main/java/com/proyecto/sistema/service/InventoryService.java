package com.proyecto.sistema.service;

import com.proyecto.sistema.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public boolean reserveProduct(Long productId, Integer quantity) {
        int updatedRows = inventoryRepository.reserveStock(productId, quantity);
        return updatedRows > 0;
    }

    @Transactional
    public void confirmPurchase(Long productId, Integer quantity) {
        inventoryRepository.confirmStock(productId, quantity);
    }

    @Transactional
    public void cancelReservation(Long productId, Integer quantity) {
        inventoryRepository.releaseStock(productId, quantity);
    }

    public Integer getAvailableStock(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(i -> i.getAvailable_stock())
                .orElse(0);
    }
}
