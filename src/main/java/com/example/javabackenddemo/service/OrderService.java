package com.example.javabackenddemo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.CreateOrderRequest;
import com.example.javabackenddemo.dto.request.UpdateOrderStatusRequest;
import com.example.javabackenddemo.dto.response.OrderListItemResponse;
import com.example.javabackenddemo.dto.response.OrderResponse;
import com.example.javabackenddemo.enums.OrderStatus;

import java.time.LocalDateTime;

public interface OrderService {
    OrderResponse createOrder(Long userId, CreateOrderRequest request);
    Page<OrderListItemResponse> listOrders(Long userId, OrderStatus status, int page, int size);
    OrderResponse getOrderDetail(Long orderId);
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
    Page<OrderListItemResponse> adminListOrders(OrderStatus status, String orderNo,
                                                 LocalDateTime startDate, LocalDateTime endDate, int page, int size);
}
