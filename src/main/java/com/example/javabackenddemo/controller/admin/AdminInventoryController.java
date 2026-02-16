package com.example.javabackenddemo.controller.admin;

import com.example.javabackenddemo.dto.request.UpdateInventoryRequest;
import com.example.javabackenddemo.dto.response.*;
import com.example.javabackenddemo.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inventory")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ApiResponse<PageResponse<InventoryResponse>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean lowStock) {
        return ApiResponse.success(PageResponse.from(inventoryService.listInventory(lowStock, PageRequest.of(page, size))));
    }

    @PutMapping("/{skuId}")
    public ApiResponse<Void> setStock(@PathVariable Long skuId, @Valid @RequestBody UpdateInventoryRequest request) {
        inventoryService.setStock(skuId, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/{skuId}/logs")
    public ApiResponse<PageResponse<InventoryLogResponse>> logs(@PathVariable Long skuId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(inventoryService.getInventoryLogs(skuId, PageRequest.of(page, size))));
    }
}
