package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.AddCartItemRequest;
import com.example.javabackenddemo.dto.response.CartItemResponse;
import com.example.javabackenddemo.dto.response.CartResponse;
import com.example.javabackenddemo.entity.*;
import com.example.javabackenddemo.exception.InsufficientStockException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.*;
import com.example.javabackenddemo.service.CartService;
import com.example.javabackenddemo.service.CurrencyService;
import com.example.javabackenddemo.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SkuRepository skuRepository;
    private final ProductRepository productRepository;
    private final SkuSpecificationRepository specRepository;
    private final InventoryService inventoryService;
    private final CurrencyService currencyService;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
                           SkuRepository skuRepository, ProductRepository productRepository,
                           SkuSpecificationRepository specRepository,
                           InventoryService inventoryService, CurrencyService currencyService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.skuRepository = skuRepository;
        this.productRepository = productRepository;
        this.specRepository = specRepository;
        this.inventoryService = inventoryService;
        this.currencyService = currencyService;
    }

    @Override
    public CartResponse getCart(Long userId, String currency) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart, currency);
    }

    @Override
    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Sku sku = skuRepository.findById(request.skuId())
                .orElseThrow(() -> new ResourceNotFoundException("SKU not found: " + request.skuId()));
        int available = inventoryService.getAvailableStock(request.skuId());
        var existing = cartItemRepository.findByCartIdAndSkuId(cart.getId(), request.skuId());
        int currentQty = existing.map(CartItem::getQuantity).orElse(0);
        if (currentQty + request.quantity() > available) {
            throw new InsufficientStockException("Insufficient stock for SKU: " + request.skuId(),
                    List.of(request.skuId()));
        }
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            cartItemRepository.save(item);
        } else {
            cartItemRepository.save(CartItem.builder()
                    .cartId(cart.getId()).skuId(request.skuId()).quantity(request.quantity()).build());
        }
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));
        cartItemRepository.delete(item);
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public void removeItemsByIds(Long userId, List<Long> cartItemIds) {
        cartItemRepository.deleteAllById(cartItemIds);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
    }

    private CartResponse buildCartResponse(Cart cart, String currency) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        BigDecimal total = BigDecimal.ZERO;
        List<CartItemResponse> itemResponses = new java.util.ArrayList<>();
        for (CartItem item : items) {
            Sku sku = skuRepository.findById(item.getSkuId()).orElse(null);
            if (sku == null) continue;
            Product product = productRepository.findById(sku.getProductId()).orElse(null);
            if (product == null) continue;
            List<SkuSpecification> specs = specRepository.findBySkuId(sku.getId());
            String specStr = specs.stream().map(s -> s.getSpecName() + ":" + s.getSpecValue())
                    .reduce((a, b) -> a + ", " + b).orElse("");
            BigDecimal price = sku.getPrice();
            if (currency != null && !currency.equals(product.getBaseCurrency())) {
                price = currencyService.convert(price, product.getBaseCurrency(), currency);
            }
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
            itemResponses.add(new CartItemResponse(item.getId(), sku.getId(), product.getName(),
                    product.getMainImage(), specStr, price, item.getQuantity(), subtotal));
        }
        return new CartResponse(cart.getId(), itemResponses, total, currency != null ? currency : "CNY");
    }
}
