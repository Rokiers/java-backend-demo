package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.UpdateInventoryRequest;
import com.example.javabackenddemo.dto.response.InventoryLogResponse;
import com.example.javabackenddemo.dto.response.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
    void deductStock(Long skuId, int quantity);
    void setStock(Long skuId, UpdateInventoryRequest request);
    Page<InventoryResponse> listInventory(boolean lowStockOnly, Pageable pageable);
    Page<InventoryLogResponse> getInventoryLogs(Long skuId, Pageable pageable);
    int getAvailableStock(Long skuId);
}
