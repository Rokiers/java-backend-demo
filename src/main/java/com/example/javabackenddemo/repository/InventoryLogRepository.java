package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    Page<InventoryLog> findByInventoryIdOrderByCreatedAtDesc(Long inventoryId, Pageable pageable);
}
