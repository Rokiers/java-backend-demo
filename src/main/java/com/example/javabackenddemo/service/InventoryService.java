package com.example.javabackenddemo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.UpdateInventoryRequest;
import com.example.javabackenddemo.dto.response.InventoryLogResponse;
import com.example.javabackenddemo.dto.response.InventoryResponse;

public interface InventoryService {
    void deductStock(Long skuId, int quantity);
    void setStock(Long skuId, UpdateInventoryRequest request);
    Page<InventoryResponse> listInventory(boolean lowStockOnly, int page, int size);
    Page<InventoryLogResponse> getInventoryLogs(Long skuId, int page, int size);
    int getAvailableStock(Long skuId);
}
