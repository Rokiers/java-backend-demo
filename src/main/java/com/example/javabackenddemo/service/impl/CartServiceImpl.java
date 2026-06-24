package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.AddCartItemRequest;
import com.example.javabackenddemo.dto.response.CartItemResponse;
import com.example.javabackenddemo.dto.response.CartResponse;
import com.example.javabackenddemo.entity.*;
import com.example.javabackenddemo.exception.InsufficientStockException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.*;
import com.example.javabackenddemo.service.CartService;
import com.example.javabackenddemo.service.CurrencyService;
import com.example.javabackenddemo.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final SkuMapper skuMapper;
    private final ProductMapper productMapper;
    private final SkuSpecificationMapper specMapper;
    private final InventoryService inventoryService;
    private final CurrencyService currencyService;

    public CartServiceImpl(CartMapper cartMapper, CartItemMapper cartItemMapper,
                           SkuMapper skuMapper, ProductMapper productMapper,
                           SkuSpecificationMapper specMapper,
                           InventoryService inventoryService, CurrencyService currencyService) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.skuMapper = skuMapper;
        this.productMapper = productMapper;
        this.specMapper = specMapper;
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
        Sku sku = skuMapper.selectById(request.skuId());
        if (sku == null) throw new ResourceNotFoundException("SKU not found: " + request.skuId());
        int available = inventoryService.getAvailableStock(request.skuId());
        var existing = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .eq(CartItem::getSkuId, request.skuId()));
        int currentQty = existing != null ? existing.getQuantity() : 0;
        if (currentQty + request.quantity() > available) {
            throw new InsufficientStockException("Insufficient stock for SKU: " + request.skuId(),
                    List.of(request.skuId()));
        }
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.quantity());
            cartItemMapper.updateById(existing);
        } else {
            cartItemMapper.insert(CartItem.builder()
                    .cartId(cart.getId()).skuId(request.skuId()).quantity(request.quantity()).build());
        }
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemMapper.selectById(itemId);
        if (item == null) throw new ResourceNotFoundException("Cart item not found: " + itemId);
        if (quantity <= 0) {
            cartItemMapper.deleteById(itemId);
        } else {
            item.setQuantity(quantity);
            cartItemMapper.updateById(item);
        }
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemMapper.selectById(itemId);
        if (item == null) throw new ResourceNotFoundException("Cart item not found: " + itemId);
        cartItemMapper.deleteById(itemId);
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public void removeItemsByIds(Long userId, List<Long> cartItemIds) {
        cartItemMapper.deleteBatchIds(cartItemIds);
    }

    private Cart getOrCreateCart(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        if (cart == null) {
            cart = Cart.builder().userId(userId).build();
            cartMapper.insert(cart);
        }
        return cart;
    }

    private CartResponse buildCartResponse(Cart cart, String currency) {
        List<CartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>().eq(CartItem::getCartId, cart.getId()));
        BigDecimal total = BigDecimal.ZERO;
        List<CartItemResponse> itemResponses = new java.util.ArrayList<>();
        for (CartItem item : items) {
            Sku sku = skuMapper.selectById(item.getSkuId());
            if (sku == null) continue;
            Product product = productMapper.selectById(sku.getProductId());
            if (product == null) continue;
            List<SkuSpecification> specs = specMapper.selectList(new LambdaQueryWrapper<SkuSpecification>().eq(SkuSpecification::getSkuId, sku.getId()));
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
