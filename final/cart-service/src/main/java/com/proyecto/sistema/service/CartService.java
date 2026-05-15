package com.proyecto.sistema.service;

import com.proyecto.sistema.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_PREFIX = "cart:";

    public void addItem(String customerId, CartItem item) {
        String key = CART_PREFIX + customerId;
        redisTemplate.opsForList().rightPush(key, item);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    public List<CartItem> getCart(String customerId) {
        String key = CART_PREFIX + customerId;
        List<Object> items = redisTemplate.opsForList().range(key, 0, -1);
        if (items == null) return List.of();
        return items.stream()
                .map(obj -> (CartItem) obj)
                .collect(Collectors.toList());
    }

    public void clearCart(String customerId) {
        String key = CART_PREFIX + customerId;
        redisTemplate.delete(key);
    }
}
