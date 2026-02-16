package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.CreateOrderRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.OrderListItemResponse;
import com.example.javabackenddemo.dto.response.OrderResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.enums.OrderStatus;
import com.example.javabackenddemo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> create(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ApiResponse<PageResponse<OrderListItemResponse>> list(@RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) OrderStatus status) {
        return ApiResponse.success(PageResponse.from(orderService.listOrders(userId, status, PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderDetail(id));
    }
}
