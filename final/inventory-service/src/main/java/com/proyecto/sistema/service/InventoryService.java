package com.proyecto.sistema.service;

import com.proyecto.sistema.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String STOCK_KEY_PREFIX = "stock:";
    private static final String LUA_RESERVE = 
        "local stock = redis.call('get', KEYS[1]) " +
        "if stock == false then return -1 end " +
        "if tonumber(stock) >= tonumber(ARGV[1]) then " +
        "  return redis.call('decrby', KEYS[1], ARGV[1]) " +
        "else " +
        "  return -2 " +
        "end";

    public boolean reserveProduct(Long productId, Integer quantity) {
        String key = STOCK_KEY_PREFIX + productId;
        
        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(LUA_RESERVE, Long.class),
            Collections.singletonList(key),
            quantity.toString()
        );

        if (result == -1) {
            // No está en Redis, intentar cargar desde DB y reintentar (o fallar rápido)
            preWarmStock(productId);
            result = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_RESERVE, Long.class),
                Collections.singletonList(key),
                quantity.toString()
            );
        }

        return result != null && result >= 0;
    }

    public void preWarmStock(Long productId) {
        inventoryRepository.findByProductId(productId).ifPresent(inventory -> {
            redisTemplate.opsForValue().set(
                STOCK_KEY_PREFIX + productId, 
                String.valueOf(inventory.getAvailable_stock())
            );
        });
    }

    @Transactional
    public void confirmPurchase(Long productId, Integer quantity) {
        // En un flujo de alto rendimiento, esto se haría asíncronamente
        // para sincronizar la DB con Redis al final o por lotes.
        inventoryRepository.confirmStock(productId, quantity);
    }

    @Transactional
    public void cancelReservation(Long productId, Integer quantity) {
        String key = STOCK_KEY_PREFIX + productId;
        redisTemplate.opsForValue().increment(key, quantity);
        inventoryRepository.releaseStock(productId, quantity);
    }

    public Integer getAvailableStock(Long productId) {
        String key = STOCK_KEY_PREFIX + productId;
        String val = redisTemplate.opsForValue().get(key);
        if (val != null) return Integer.parseInt(val);

        return inventoryRepository.findByProductId(productId)
                .map(i -> i.getAvailable_stock())
                .orElse(0);
    }
}
