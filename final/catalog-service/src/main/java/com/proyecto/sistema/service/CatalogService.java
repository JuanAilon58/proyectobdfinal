package com.proyecto.sistema.service;

import com.proyecto.sistema.entity.Product;
import com.proyecto.sistema.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Cacheable(value = "products", key = "#name")
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
