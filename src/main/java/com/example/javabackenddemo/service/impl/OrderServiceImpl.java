package com.example.javabackenddemo.service.impl;

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
import com.example.javabackenddemo.repository.*;
import com.example.javabackenddemo.service.CartService;
import com.example.javabackenddemo.service.InventoryService;
import com.example.javabackenddemo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final SkuRepository skuRepository;
    private final ProductRepository productRepository;
    private final SkuSpecificationRepository specRepository;
    private final InventoryService inventoryService;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            CartItemRepository cartItemRepository, SkuRepository skuRepository,
                            ProductRepository productRepository, SkuSpecificationRepository specRepository,
                            InventoryService inventoryService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.skuRepository = skuRepository;
        this.productRepository = productRepository;
        this.specRepository = specRepository;
        this.inventoryService = inventoryService;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findAllById(request.cartItemIds());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No cart items found");
        }
        // Check stock for all items first
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
        // Create order
        String orderNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .currency("CNY")
                .shippingAddress(request.shippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .build();
        order = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Sku sku = skuRepository.findById(cartItem.getSkuId())
                    .orElseThrow(() -> new ResourceNotFoundException("SKU not found"));
            Product product = productRepository.findById(sku.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            List<SkuSpecification> specs = specRepository.findBySkuId(sku.getId());
            String specSnapshot = specs.stream()
                    .map(s -> s.getSpecName() + ":" + s.getSpecValue())
                    .reduce((a, b) -> a + ", " + b).orElse("");
            BigDecimal subtotal = sku.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .skuId(sku.getId())
                    .productNameSnapshot(product.getName())
                    .skuSpecSnapshot(specSnapshot)
                    .unitPrice(sku.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();
            orderItemRepository.save(orderItem);
            // Deduct stock
            inventoryService.deductStock(sku.getId(), cartItem.getQuantity());
        }
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        // Clear cart items
        cartService.removeItemsByIds(userId, request.cartItemIds());
        return toOrderResponse(order);
    }

    @Override
    public Page<OrderListItemResponse> listOrders(Long userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            orders = orderRepository.findByUserId(userId, pageable);
        }
        return orders.map(o -> new OrderListItemResponse(o.getId(), o.getOrderNo(),
                o.getStatus().name(), o.getTotalAmount(), o.getCurrency(), o.getCreatedAt()));
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        OrderStatus targetStatus = OrderStatus.valueOf(request.status());
        if (!order.getStatus().canTransitionTo(targetStatus)) {
            throw new InvalidStateTransitionException(
                    "Cannot transition from " + order.getStatus() + " to " + targetStatus);
        }
        order.setStatus(targetStatus);
        if (request.trackingNumber() != null) {
            order.setTrackingNumber(request.trackingNumber());
        }
        order = orderRepository.save(order);
        return toOrderResponse(order);
    }

    @Override
    public Page<OrderListItemResponse> adminListOrders(OrderStatus status, String orderNo,
                                                        LocalDateTime startDate, LocalDateTime endDate,
                                                        Pageable pageable) {
        return orderRepository.findByFilters(status, orderNo, startDate, endDate, pageable)
                .map(o -> new OrderListItemResponse(o.getId(), o.getOrderNo(),
                        o.getStatus().name(), o.getTotalAmount(), o.getCurrency(), o.getCreatedAt()));
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemResponses = items.stream()
                .map(i -> new OrderItemResponse(i.getId(), i.getSkuId(), i.getProductNameSnapshot(),
                        i.getSkuSpecSnapshot(), i.getUnitPrice(), i.getQuantity(), i.getSubtotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getOrderNo(), order.getTotalAmount(),
                order.getCurrency(), order.getStatus().name(), order.getShippingAddress(),
                order.getTrackingNumber(), itemResponses, order.getCreatedAt());
    }
}
