package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.UpdateInventoryRequest;
import com.example.javabackenddemo.dto.response.InventoryLogResponse;
import com.example.javabackenddemo.dto.response.InventoryResponse;
import com.example.javabackenddemo.entity.Inventory;
import com.example.javabackenddemo.entity.InventoryLog;
import com.example.javabackenddemo.enums.InventoryChangeType;
import com.example.javabackenddemo.exception.InsufficientStockException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.InventoryLogRepository;
import com.example.javabackenddemo.repository.InventoryRepository;
import com.example.javabackenddemo.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository logRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryLogRepository logRepository) {
        this.inventoryRepository = inventoryRepository;
        this.logRepository = logRepository;
    }

    @Override
    @Transactional
    public void deductStock(Long skuId, int quantity) {
        Inventory inventory = inventoryRepository.findBySkuId(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for SKU: " + skuId));
        int newQty = inventory.getQuantity() - quantity;
        if (newQty < 0) {
            throw new InsufficientStockException("Insufficient stock for SKU: " + skuId, List.of(skuId));
        }
        int oldQty = inventory.getQuantity();
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);
        logRepository.save(InventoryLog.builder()
                .inventoryId(inventory.getId())
                .changeQuantity(-quantity)
                .afterQuantity(newQty)
                .changeType(InventoryChangeType.ORDER_DEDUCT)
                .remark("Order deduction")
                .build());
    }

    @Override
    @Transactional
    public void setStock(Long skuId, UpdateInventoryRequest request) {
        Inventory inventory = inventoryRepository.findBySkuId(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for SKU: " + skuId));
        int oldQty = inventory.getQuantity();
        int change = request.quantity() - oldQty;
        inventory.setQuantity(request.quantity());
        if (request.alertThreshold() != null) inventory.setAlertThreshold(request.alertThreshold());
        inventoryRepository.save(inventory);
        logRepository.save(InventoryLog.builder()
                .inventoryId(inventory.getId())
                .changeQuantity(change)
                .afterQuantity(request.quantity())
                .changeType(InventoryChangeType.MANUAL_SET)
                .remark("Manual stock set")
                .build());
    }

    @Override
    public Page<InventoryResponse> listInventory(boolean lowStockOnly, Pageable pageable) {
        Page<Inventory> page;
        if (lowStockOnly) {
            page = inventoryRepository.findLowStock(pageable);
        } else {
            page = inventoryRepository.findAll(pageable);
        }
        return page.map(inv -> new InventoryResponse(
                inv.getSku().getId(), inv.getSku().getSkuCode(),
                inv.getQuantity(), inv.getAlertThreshold(),
                inv.getQuantity() < inv.getAlertThreshold()));
    }

    @Override
    public Page<InventoryLogResponse> getInventoryLogs(Long skuId, Pageable pageable) {
        Inventory inventory = inventoryRepository.findBySkuId(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for SKU: " + skuId));
        return logRepository.findByInventoryIdOrderByCreatedAtDesc(inventory.getId(), pageable)
                .map(log -> new InventoryLogResponse(log.getId(), log.getChangeQuantity(),
                        log.getAfterQuantity(), log.getChangeType().name(), log.getRemark(), log.getCreatedAt()));
    }

    @Override
    public int getAvailableStock(Long skuId) {
        return inventoryRepository.findBySkuId(skuId).map(Inventory::getQuantity).orElse(0);
    }
}
