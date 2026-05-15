package com.proyecto.sistema.repository;

import com.proyecto.sistema.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByProductId(Long productId);

    @Modifying
    @Query("UPDATE Inventory i SET i.available_stock = i.available_stock - :quantity, " +
           "i.reserved_stock = i.reserved_stock + :quantity, " +
           "i.updated_at = CURRENT_TIMESTAMP " +
           "WHERE i.productId = :productId AND i.available_stock >= :quantity")
    int reserveStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Inventory i SET i.reserved_stock = i.reserved_stock - :quantity, " +
           "i.updated_at = CURRENT_TIMESTAMP " +
           "WHERE i.productId = :productId AND i.reserved_stock >= :quantity")
    int confirmStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Inventory i SET i.available_stock = i.available_stock + :quantity, " +
           "i.reserved_stock = i.reserved_stock - :quantity, " +
           "i.updated_at = CURRENT_TIMESTAMP " +
           "WHERE i.productId = :productId AND i.reserved_stock >= :quantity")
    int releaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
