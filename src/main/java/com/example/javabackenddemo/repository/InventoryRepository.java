package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuId(Long skuId);

    @Query("SELECT i FROM Inventory i WHERE i.quantity < i.alertThreshold")
    Page<Inventory> findLowStock(Pageable pageable);
}
