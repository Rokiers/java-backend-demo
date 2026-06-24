package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.CreateOrderRequest;
import com.example.javabackenddemo.dto.request.UpdateOrderStatusRequest;
import com.example.javabackenddemo.dto.response.OrderItemResponse;
import com.example.javabackenddemo.dto.response.OrderListItemResponse;
import com.example.javabackenddemo.dto.response.OrderResponse;
import com.example.javabackenddemo.entity.*;
import com.example.javabackenddemo.enums.OrderStatus;
import com.example.javabackenddemo.exception.InsufficientStockException;
import com.example.javabackenddemo.exception.InvalidStateTransitionException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.*;
import com.example.javabackenddemo.service.CartService;
import com.example.javabackenddemo.service.InventoryService;
import com.example.javabackenddemo.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final SkuMapper skuMapper;
    private final ProductMapper productMapper;
    private final SkuSpecificationMapper specMapper;
    private final InventoryService inventoryService;
    private final CartService cartService;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                            CartItemMapper cartItemMapper, SkuMapper skuMapper,
                            ProductMapper productMapper, SkuSpecificationMapper specMapper,
                            InventoryService inventoryService, CartService cartService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartItemMapper = cartItemMapper;
        this.skuMapper = skuMapper;
        this.productMapper = productMapper;
        this.specMapper = specMapper;
        this.inventoryService = inventoryService;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        List<CartItem> cartItems = cartItemMapper.selectBatchIds(request.cartItemIds());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No cart items found");
        }
        List<Long> insufficientSkus = new ArrayList<>();
        for (CartItem item : cartItems) {
            int available = inventoryService.getAvailableStock(item.getSkuId());
            if (available < item.getQuantity()) {
                insufficientSkus.add(item.getSkuId());
            }
        }
        if (!insufficientSkus.isEmpty()) {
            throw new InsufficientStockException("Insufficient stock", insufficientSkus);
        }
        String orderNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .currency("CNY")
                .shippingAddress(request.shippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .build();
        orderMapper.insert(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Sku sku = skuMapper.selectById(cartItem.getSkuId());
            if (sku == null) throw new ResourceNotFoundException("SKU not found");
            Product product = productMapper.selectById(sku.getProductId());
            if (product == null) throw new ResourceNotFoundException("Product not found");
            List<SkuSpecification> specs = specMapper.selectList(
                    new LambdaQueryWrapper<SkuSpecification>().eq(SkuSpecification::getSkuId, sku.getId()));
            String specSnapshot = specs.stream()
                    .map(s -> s.getSpecName() + ":" + s.getSpecValue())
                    .reduce((a, b) -> a + ", " + b).orElse("");
            BigDecimal subtotal = sku.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            orderItemMapper.insert(OrderItem.builder()
                    .orderId(order.getId())
                    .skuId(sku.getId())
                    .productNameSnapshot(product.getName())
                    .skuSpecSnapshot(specSnapshot)
                    .unitPrice(sku.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build());
            inventoryService.deductStock(sku.getId(), cartItem.getQuantity());
        }
        order.setTotalAmount(totalAmount);
        orderMapper.updateById(order);
        cartService.removeItemsByIds(userId, request.cartItemIds());
        return toOrderResponse(order);
    }

    @Override
    public Page<OrderListItemResponse> listOrders(Long userId, OrderStatus status, int page, int size) {
        Page<Order> myPage = new Page<>(page + 1, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId);
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getCreatedAt);
        Page<Order> orders = orderMapper.selectPage(myPage, wrapper);
        return convertPage(orders, o -> new OrderListItemResponse(o.getId(), o.getOrderNo(),
                o.getStatus().name(), o.getTotalAmount(), o.getCurrency(), o.getCreatedAt()));
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new ResourceNotFoundException("Order not found: " + orderId);
        return toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new ResourceNotFoundException("Order not found: " + orderId);
        OrderStatus targetStatus = OrderStatus.valueOf(request.status());
        if (!order.getStatus().canTransitionTo(targetStatus)) {
            throw new InvalidStateTransitionException(
                    "Cannot transition from " + order.getStatus() + " to " + targetStatus);
        }
        order.setStatus(targetStatus);
        if (request.trackingNumber() != null) {
            order.setTrackingNumber(request.trackingNumber());
        }
        orderMapper.updateById(order);
        return toOrderResponse(order);
    }

    @Override
    public Page<OrderListItemResponse> adminListOrders(OrderStatus status, String orderNo,
                                                        LocalDateTime startDate, LocalDateTime endDate,
                                                        int page, int size) {
        Page<Order> myPage = new Page<>(page + 1, size);
        Page<Order> orders = orderMapper.findByFilters(myPage, status, orderNo, startDate, endDate);
        return convertPage(orders, o -> new OrderListItemResponse(o.getId(), o.getOrderNo(),
                o.getStatus().name(), o.getTotalAmount(), o.getCurrency(), o.getCreatedAt()));
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        List<OrderItemResponse> itemResponses = items.stream()
                .map(i -> new OrderItemResponse(i.getId(), i.getSkuId(), i.getProductNameSnapshot(),
                        i.getSkuSpecSnapshot(), i.getUnitPrice(), i.getQuantity(), i.getSubtotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getOrderNo(), order.getTotalAmount(),
                order.getCurrency(), order.getStatus().name(), order.getShippingAddress(),
                order.getTrackingNumber(), itemResponses, order.getCreatedAt());
    }

    @SuppressWarnings("unchecked")
    private <T, R> Page<R> convertPage(Page<T> source, java.util.function.Function<T, R> mapper) {
        Page<R> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(mapper).toList());
        return result;
    }
}
