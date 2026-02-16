package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.AddCartItemRequest;
import com.example.javabackenddemo.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId, String currency);
    CartResponse addItem(Long userId, AddCartItemRequest request);
    CartResponse updateItemQuantity(Long userId, Long itemId, int quantity);
    CartResponse removeItem(Long userId, Long itemId);
    void removeItemsByIds(Long userId, java.util.List<Long> cartItemIds);
}
