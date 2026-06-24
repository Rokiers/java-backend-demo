package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.UpdateInventoryRequest;
import com.example.javabackenddemo.dto.response.InventoryLogResponse;
import com.example.javabackenddemo.dto.response.InventoryResponse;
import com.example.javabackenddemo.entity.Inventory;
import com.example.javabackenddemo.entity.InventoryLog;
import com.example.javabackenddemo.enums.InventoryChangeType;
import com.example.javabackenddemo.exception.InsufficientStockException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.InventoryLogMapper;
import com.example.javabackenddemo.mapper.InventoryMapper;
import com.example.javabackenddemo.mapper.SkuMapper;
import com.example.javabackenddemo.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper logMapper;
    private final SkuMapper skuMapper;

    public InventoryServiceImpl(InventoryMapper inventoryMapper, InventoryLogMapper logMapper,
                                SkuMapper skuMapper) {
        this.inventoryMapper = inventoryMapper;
        this.logMapper = logMapper;
        this.skuMapper = skuMapper;
    }

    @Override
    @Transactional
    public void deductStock(Long skuId, int quantity) {
        Inventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));
        if (inventory == null) throw new ResourceNotFoundException("Inventory not found for SKU: " + skuId);
        int newQty = inventory.getQuantity() - quantity;
        if (newQty < 0) {
            throw new InsufficientStockException("Insufficient stock for SKU: " + skuId, List.of(skuId));
        }
        int oldQty = inventory.getQuantity();
        inventory.setQuantity(newQty);
        inventoryMapper.updateById(inventory);
        logMapper.insert(InventoryLog.builder()
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
        Inventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));
        if (inventory == null) throw new ResourceNotFoundException("Inventory not found for SKU: " + skuId);
        int oldQty = inventory.getQuantity();
        int change = request.quantity() - oldQty;
        inventory.setQuantity(request.quantity());
        if (request.alertThreshold() != null) inventory.setAlertThreshold(request.alertThreshold());
        inventoryMapper.updateById(inventory);
        logMapper.insert(InventoryLog.builder()
                .inventoryId(inventory.getId())
                .changeQuantity(change)
                .afterQuantity(request.quantity())
                .changeType(InventoryChangeType.MANUAL_SET)
                .remark("Manual stock set")
                .build());
    }

    @Override
    public Page<InventoryResponse> listInventory(boolean lowStockOnly, int page, int size) {
        Page<Inventory> myPage = new Page<>(page + 1, size);
        Page<Inventory> result;
        if (lowStockOnly) {
            List<Inventory> all = inventoryMapper.findLowStock();
            result = new Page<>(page + 1, size, all.size());
            int from = Math.min(page * size, all.size());
            int to = Math.min(from + size, all.size());
            result.setRecords(all.subList(from, to));
        } else {
            result = inventoryMapper.selectPage(myPage, null);
        }
        return convertPage(result, inv -> {
            var sku = skuMapper.selectById(inv.getSkuId());
            return new InventoryResponse(inv.getSkuId(), sku != null ? sku.getSkuCode() : "",
                    inv.getQuantity(), inv.getAlertThreshold(),
                    inv.getQuantity() < inv.getAlertThreshold());
        });
    }

    @Override
    public Page<InventoryLogResponse> getInventoryLogs(Long skuId, int page, int size) {
        Inventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));
        if (inventory == null) throw new ResourceNotFoundException("Inventory not found for SKU: " + skuId);
        Page<InventoryLog> myPage = new Page<>(page + 1, size);
        Page<InventoryLog> logs = logMapper.selectPage(myPage,
                new LambdaQueryWrapper<InventoryLog>().eq(InventoryLog::getInventoryId, inventory.getId())
                        .orderByDesc(InventoryLog::getCreatedAt));
        return convertPage(logs, log -> new InventoryLogResponse(log.getId(), log.getChangeQuantity(),
                log.getAfterQuantity(), log.getChangeType().name(), log.getRemark(), log.getCreatedAt()));
    }

    @Override
    public int getAvailableStock(Long skuId) {
        Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));
        return inv != null ? inv.getQuantity() : 0;
    }

    @SuppressWarnings("unchecked")
    private <T, R> Page<R> convertPage(Page<T> source, java.util.function.Function<T, R> mapper) {
        Page<R> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(mapper).toList());
        return result;
    }
}
